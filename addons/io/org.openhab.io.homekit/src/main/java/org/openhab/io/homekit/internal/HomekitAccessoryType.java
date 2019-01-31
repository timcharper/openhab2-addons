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
 * Enum of the possible device types. The defined tag string can be used
 * as a tag on an item to enable it for Homekit.
 *
 * @author Andy Lintner
 */
public enum HomekitAccessoryType {

    DIMMABLE_LIGHTBULB("DimmableLighting"),
    HUMIDITY_SENSOR("CurrentHumidity"),
    LIGHTBULB("Lighting"),
    SWITCH("Switchable"),
    TEMPERATURE_SENSOR("CurrentTemperature"),
    THERMOSTAT("Thermostat"),
    COLORFUL_LIGHTBULB("ColorfulLighting"),
    VALVE("Valve"),
    LEAK_SENSOR("LeakSensor"),
    MOTION_SENSOR("MotionSensor"),
    OCCUPANCY_SENSOR("OccupancySensor"),
    CONTACT_SENSOR("ContactSensor"),
    WINDOW_COVERING("WindowCovering"),
    SMOKE_SENSOR("SmokeSensor"),
    CARBON_MONOXIDE_SENSOR("CarbonMonoxideSensor"),
    @Deprecated()
    BLINDS("Blinds");

    private static final Map<String, HomekitAccessoryType> tagMap = new HashMap<>();

    static {
        for (HomekitAccessoryType type : HomekitAccessoryType.values()) {
            tagMap.put(type.tag, type);
        }
    }

    private final String tag;

    private HomekitAccessoryType(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public static HomekitAccessoryType valueOfTag(String tag) {
        return tagMap.get(tag);
    }
}
