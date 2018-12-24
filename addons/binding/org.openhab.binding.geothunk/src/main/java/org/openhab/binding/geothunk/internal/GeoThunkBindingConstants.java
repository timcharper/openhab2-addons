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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Temperature;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@link GeoThunkBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Tim Harper - Initial contribution
 */
@NonNullByDefault
public class GeoThunkBindingConstants {

    private static final String BINDING_ID = "geothunk";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_AQI = new ThingTypeUID(BINDING_ID, "aqi");

    // List of all Channel ids
    public static final String PM1 = "pm1";
    public static final String PM25 = "pm25";
    public static final String PM10 = "pm10";
    public static final String HUMIDITY = "humidity";
    public static final String TEMPERATURE = "temperature";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_AQI);
    public static final Set<String> SUPPORTED_CHANNEL_IDS = Stream.of(PM1, PM25, PM10, HUMIDITY, TEMPERATURE)
            .collect(Collectors.toSet());

    // Units of measurement of the data delivered by the API
    public static final Unit<Temperature> API_TEMPERATURE_UNIT = SIUnits.CELSIUS;
    public static final Unit<Dimensionless> API_HUMIDITY_UNIT = SmartHomeUnits.PERCENT;
}
