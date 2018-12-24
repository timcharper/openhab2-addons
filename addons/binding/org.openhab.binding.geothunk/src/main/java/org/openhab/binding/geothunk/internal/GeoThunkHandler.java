/**
 * Copyright (c) 2014,2018 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.geothunk.internal;

import static org.openhab.binding.geothunk.internal.GeoThunkBindingConstants.*;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.geothunk.internal.GeoThunkConfiguration;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.quantity.Temperature;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The {@link GeoThunkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Tim Harper - Initial contribution
 */
@NonNullByDefault
public class GeoThunkHandler extends BaseThingHandler {

    private static final int DEFAULT_REFRESH_PERIOD_SECONDS = 600; // 10 minutes
    private final Logger logger = LoggerFactory.getLogger(GeoThunkHandler.class);

    @Nullable
    private GeoThunkConfiguration config;

    private ScheduledFuture<?> refreshJob;

    private GeoThunkJsonResponse csResponse;

    private Gson gson;

    private int retryCounter = 0;

    public GeoThunkHandler(Thing thing) {
        super(thing);
        gson = new Gson();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            updateChannel(channelUID.getId(), csResponse);
        } else {
            logger.debug("The GeoThunk binding is read-only and can not handle command {}", command);
        }
    }

    /**
     * Update the channel from the last Air Quality data retrieved
     *
     * @param channelId the id identifying the channel to be updated
     */
    private void updateChannel(String channelId, GeoThunkJsonResponse response) {
        if (isLinked(channelId)) {
            Object value;
            try {
                value = getValue(channelId, response);
            } catch (Exception e) {
                logger.debug("Station doesn't provide {} measurement", channelId.toUpperCase());
                return;
            }

            State state = null;
            if (value == null) {
                state = UnDefType.UNDEF;
            } else if (value instanceof QuantityType<?>) {
                state = (QuantityType<?>) value;
            } else if (value instanceof BigDecimal) {
                state = new DecimalType((BigDecimal) value);
            } else if (value instanceof Integer) {
                state = new DecimalType(BigDecimal.valueOf(((Integer) value).longValue()));
            } else if (value instanceof String) {
                state = new StringType(value.toString());
            } else {
                logger.warn("Update channel {}: Unsupported value type {}", channelId,
                        value.getClass().getSimpleName());
            }
            logger.debug("Update channel {} with state {} ({})", channelId, (state == null) ? "null" : state.toString(),
                    (value == null) ? "null" : value.getClass().getSimpleName());

            // Update the channel
            if (state != null) {
                updateState(channelId, state);
            }
        }
    }

    public static Object getValue(String channelId, GeoThunkJsonResponse data) throws Exception {
        String[] fields = StringUtils.split(channelId, "#");

        if (data != null) {
            switch (fields[0]) {
                case PM1:
                    return data.getPm1();
                case PM25:
                    return data.getPm2_5();
                case PM10:
                    return data.getPm10();
                case TEMPERATURE:
                    return new QuantityType<Temperature>(data.getTemperatureCelsius(), API_TEMPERATURE_UNIT);
                case HUMIDITY:
                    return new QuantityType<>(data.getHumidityPercent(), API_HUMIDITY_UNIT);
            }
        }

        return null;
    }


    @Override
    public void initialize() {
        // logger.debug("Start initializing!");
        config = getConfigAs(GeoThunkConfiguration.class);
        logger.debug("config url = {}", config.url);
        logger.debug("config refreshSeconds = {}", config.refreshSeconds);

        String errorMsg = null;
        if (StringUtils.trimToNull(config.url) == null) {
            errorMsg = "Parameter 'url' is mandatory and must be configured";
        }
        if (errorMsg == null) {
            updateStatus(ThingStatus.ONLINE);
            startAutomaticRefresh();
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, errorMsg);
        }
    }

    /**
     * Start the job refreshing the Air Quality data
     */
    private void startAutomaticRefresh() {
        if (refreshJob == null || refreshJob.isCancelled()) {
            Runnable runnable = () -> {
                try {
                    // Request new air quality data to the aqicn.org service
                    retryCounter = 0;
                    csResponse = getGeoThunkData();

                    // Update all channels from the updated AQI data
                    for (Channel channel : getThing().getChannels()) {
                        updateChannel(channel.getUID().getId(), csResponse);
                    }
                } catch (Exception e) {
                    logger.error("Exception occurred during execution: {}", e.getMessage(), e);
                }
            };

            GeoThunkConfiguration config = getConfigAs(GeoThunkConfiguration.class);
            int delay = (config.refreshSeconds != null) ? config.refreshSeconds : DEFAULT_REFRESH_PERIOD_SECONDS;
            refreshJob = scheduler.scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.SECONDS);
        }
    }

    private String buildRequestURL() {
        String url = config.url;
        if (!config.url.endsWith("/"))
            url = url + "/";

        return url + "stats";
    }
    /**
     * Request new air quality data to the aqicn.org service
     *
     * @return the air quality data object mapping the JSON response or null in case of error
     */
    private GeoThunkJsonResponse getGeoThunkData() {
        GeoThunkJsonResponse result = null;
        String errorMsg = null;

        String urlStr = buildRequestURL();
        logger.debug("URL = {}", urlStr);

        try {
            // Run the HTTP request and get the JSON response from aqicn.org
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();

            try {
                String response = IOUtils.toString(connection.getInputStream());
                logger.debug("aqiResponse = {}", response);

                // Map the JSON response to an object
                result = gson.fromJson(response, GeoThunkJsonResponse.class);
            } finally {
                IOUtils.closeQuietly(connection.getInputStream());
            }

            if (result.getHumidityPercent() > 0) {
                String description = String.format("LastUpdate: %d; Humidity: %d; Temperature: %d; PM25: %d", result.getLastUpdate(), result.getHumidityPercent(), result.getTemperatureCelsius(), result.getPm2_5());
                updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE, description);
                return result;
            } else {
                retryCounter++;
                logger.warn("Error in geothunk binding ({}); humidity was 0. Is a wire loose? Retrying", urlStr);
                return getGeoThunkData();
            }

        } catch (MalformedURLException e) {
            errorMsg = e.getMessage();
            logger.warn("Constructed url {} is not valid: {}", urlStr, errorMsg);
        } catch (JsonSyntaxException e) {
            errorMsg = "Configuration is incorrect";
            logger.warn("Error running geothunk binding (Air Quality) request: {}", errorMsg);
        } catch (IOException | IllegalStateException e) {
            errorMsg = e.getMessage();
        }

        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, errorMsg);
        return null;
    }
}
