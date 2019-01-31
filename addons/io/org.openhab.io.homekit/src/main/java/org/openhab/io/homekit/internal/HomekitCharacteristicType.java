/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.homekit.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Characteristics are used by complex accessories that can't be represented by
 * a single item (i.e. a thermostat)
 *
 * @author Andy Lintner - Initial contribution
 */
public enum HomekitCharacteristicType {
    /*
     * It is illegal to have a characteristic type also be a device type
     */
    BATTERY_LEVEL("homekit:BatteryLevel"),
    TARGET_TEMPERATURE("homekit:TargetTemperature"),
    @Deprecated()
    OLD_TARGET_TEMPERATURE("TargetTemperature"),
    BATTERY_LOW_STATUS("homekit:BatteryLowStatus"),
    @Deprecated()
    OLD_TARGET_HEATING_COOLING_MODE("homekit:HeatingCoolingMode"),
    TARGET_HEATING_COOLING_MODE("homekit:TargetHeatingCoolingMode"),
    CURRENT_HEATING_COOLING_STATE("homekit:CurrentHeatingCoolingMode");

    private static final Map<String, HomekitCharacteristicType> tagMap = new HashMap<>();

    static {
        for (HomekitCharacteristicType type : HomekitCharacteristicType.values()) {
            tagMap.put(type.tag, type);
        }
    }

    private final String tag;

    private HomekitCharacteristicType(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public static HomekitCharacteristicType valueOfTag(String tag) {
        return tagMap.get(tag);
    }
}
