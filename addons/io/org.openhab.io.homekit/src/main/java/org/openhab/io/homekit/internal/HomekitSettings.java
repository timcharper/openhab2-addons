/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.homekit.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.Optional;

import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the configured and static settings for the Homekit addon
 *
 * @author Andy Lintner
 */
public class HomekitSettings {

    private static final String NAME = "openHAB";
    private static final String MANUFACTURER = "openHAB";
    private static final String SERIAL_NUMBER = "none";

    /* Name under which openHAB announces itself as HomeKit bridge (#1946) */
    private String name = NAME;
    private int port = 9123;
    private String pin = "031-45-154";
    private boolean useFahrenheitTemperature = false;
    private double minimumTemperature = -100;
    private double maximumTemperature = 100;
    private String thermostatTargetModeHeat = "HeatOn";
    private String thermostatTargetModeCool = "CoolOn";
    private String thermostatTargetModeAuto = "Auto";
    private String thermostatTargetModeOff = "Off";
    private String thermostatCurrentModeHeating = "Heating";
    private String thermostatCurrentModeCooling = "Cooling";
    private String thermostatCurrentModeOff = "Off";
    private InetAddress networkInterface;

    private final Logger logger = LoggerFactory.getLogger(HomekitSettings.class);

    public void fill(Dictionary<String, ?> properties) throws UnknownHostException {
        Object name = properties.get("name");
        if (name instanceof String && ((String) name).length() > 0) {
            this.name = (String) name;
        }
        Object port = properties.get("port");
        if (port instanceof Integer) {
            this.port = (Integer) port;
        } else if (port instanceof String) {
            String portString = (String) properties.get("port");
            if (portString != null) {
                this.port = Integer.parseInt(portString);
            }
        }
        this.pin = getOrDefault(properties.get("pin"), this.pin);
        Object useFahrenheitTemperature = properties.get("useFahrenheitTemperature");
        if (useFahrenheitTemperature instanceof Boolean) {
            this.useFahrenheitTemperature = (Boolean) useFahrenheitTemperature;
        } else if (useFahrenheitTemperature instanceof String) {
            String useFahrenheitTemperatureString = (String) properties.get("useFahrenheitTemperature");
            if (useFahrenheitTemperatureString != null) {
                this.useFahrenheitTemperature = Boolean.valueOf(useFahrenheitTemperatureString);
            }
        }
        Object minimumTemperature = properties.get("minimumTemperature");
        if (minimumTemperature != null) {
            this.minimumTemperature = Double.parseDouble(minimumTemperature.toString());
        }
        Object maximumTemperature = properties.get("maximumTemperature");
        if (maximumTemperature != null) {
            this.maximumTemperature = Double.parseDouble(maximumTemperature.toString());
        }
        this.thermostatTargetModeHeat = Optional.ofNullable((String) properties.get("thermostatTargetModeHeat"))
                .orElseGet(() -> (String) properties.get("thermostatHeatMode" /* legacy setting */));
        this.thermostatTargetModeCool = Optional.ofNullable((String) properties.get("thermostatTargetModeCool"))
                .orElseGet(() -> (String) properties.get("thermostatCoolMode" /* legacy setting */));
        this.thermostatTargetModeAuto = Optional.ofNullable((String) properties.get("thermostatTargetModeAuto"))
                .orElseGet(() -> (String) properties.get("thermostatAutoMode" /* legacy setting */));
        this.thermostatTargetModeOff = Optional.ofNullable((String) properties.get("thermostatTargetModeOff"))
                .orElseGet(() -> (String) properties.get("thermostatOffMode" /* legacy setting */));

        Optional.ofNullable((String) properties.get("thermostatCurrentModeCooling"))
                .ifPresent(value -> this.thermostatCurrentModeCooling = value);
        Optional.ofNullable((String) properties.get("thermostatCurrentModeHeating"))
                .ifPresent(value -> this.thermostatCurrentModeHeating = value);
        Optional.ofNullable((String) properties.get("thermostatCurrentModeOff"))
                .ifPresent(value -> this.thermostatCurrentModeOff = value);

        String networkInterface = (String) properties.get("networkInterface");
        if (networkInterface == null) {
            this.networkInterface = InetAddress.getLocalHost();
        } else {
            this.networkInterface = InetAddress.getByName(networkInterface);
        }
    }

    private static String getOrDefault(Object value, String defaultValue) {
        return value != null ? (String) value : defaultValue;
    }

    public String getName() {
        logger.debug("Using homekit name '{}'", name);
        return name;
    }

    public String getManufacturer() {
        return MANUFACTURER;
    }

    public String getSerialNumber() {
        return SERIAL_NUMBER;
    }

    public String getModel() {
        return FrameworkUtil.getBundle(getClass()).getVersion().toString();
    }

    public InetAddress getNetworkInterface() {
        return networkInterface;
    }

    public int getPort() {
        return port;
    }

    public String getPin() {
        return pin;
    }

    public boolean useFahrenheitTemperature() {
        return useFahrenheitTemperature;
    }

    public double getMaximumTemperature() {
        return maximumTemperature;
    }

    public double getMinimumTemperature() {
        return minimumTemperature;
    }

    public String getThermostatTargetModeHeat() {
        return thermostatTargetModeHeat;
    }

    public String getThermostatTargetModeCool() {
        return thermostatTargetModeCool;
    }

    public String getThermostatTargetModeAuto() {
        return thermostatTargetModeAuto;
    }

    public String getThermostatTargetModeOff() {
        return thermostatTargetModeOff;
    }

    public String getThermostatCurrentModeHeating() {
        return thermostatCurrentModeHeating;
    }

    public String getThermostatCurrentModeCooling() {
        return thermostatCurrentModeCooling;
    }

    public String getCurrentModeOff() {
        return thermostatCurrentModeOff;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(maximumTemperature);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minimumTemperature);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((pin == null) ? 0 : pin.hashCode());
        result = prime * result + port;
        result = prime * result + ((thermostatTargetModeAuto == null) ? 0 : thermostatTargetModeAuto.hashCode());
        result = prime * result + ((thermostatTargetModeCool == null) ? 0 : thermostatTargetModeCool.hashCode());
        result = prime * result + ((thermostatTargetModeHeat == null) ? 0 : thermostatTargetModeHeat.hashCode());
        result = prime * result + ((thermostatTargetModeOff == null) ? 0 : thermostatTargetModeOff.hashCode());
        result = prime * result + (useFahrenheitTemperature ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        HomekitSettings other = (HomekitSettings) obj;
        if (Double.doubleToLongBits(maximumTemperature) != Double.doubleToLongBits(other.maximumTemperature)) {
            return false;
        }
        if (Double.doubleToLongBits(minimumTemperature) != Double.doubleToLongBits(other.minimumTemperature)) {
            return false;
        }
        if (pin == null) {
            if (other.pin != null) {
                return false;
            }
        } else if (!pin.equals(other.pin)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        if (thermostatTargetModeAuto == null) {
            if (other.thermostatTargetModeAuto != null) {
                return false;
            }
        } else if (!thermostatTargetModeAuto.equals(other.thermostatTargetModeAuto)) {
            return false;
        }
        if (thermostatTargetModeCool == null) {
            if (other.thermostatTargetModeCool != null) {
                return false;
            }
        } else if (!thermostatTargetModeCool.equals(other.thermostatTargetModeCool)) {
            return false;
        }
        if (thermostatTargetModeHeat == null) {
            if (other.thermostatTargetModeHeat != null) {
                return false;
            }
        } else if (!thermostatTargetModeHeat.equals(other.thermostatTargetModeHeat)) {
            return false;
        }
        if (thermostatTargetModeOff == null) {
            if (other.thermostatTargetModeOff != null) {
                return false;
            }
        } else if (!thermostatTargetModeOff.equals(other.thermostatTargetModeOff)) {
            return false;
        }
        if (useFahrenheitTemperature != other.useFahrenheitTemperature) {
            return false;
        }
        return true;
    }

}
