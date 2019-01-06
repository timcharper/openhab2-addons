/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.homekit.internal.accessories;

import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.link.ItemChannelLinkRegistry;
import org.openhab.io.homekit.internal.HomekitAccessoryUpdater;
import org.openhab.io.homekit.internal.HomekitSettings;
import org.openhab.io.homekit.internal.HomekitTaggedItem;

import com.beowulfe.hap.HomekitAccessory;

/**
 * Creates a HomekitAccessory for a given HomekitTaggedItem.
 *
 * @author Andy Lintner
 */
public class HomekitAccessoryFactory {

    public static HomekitAccessory create(HomekitTaggedItem taggedItem, ItemRegistry itemRegistry,
            ItemChannelLinkRegistry itemChannelLinkRegistry, HomekitAccessoryUpdater updater, HomekitSettings settings)
            throws Exception {
        switch (taggedItem.getDeviceType()) {
            case LEAK_SENSOR:
                return new HomekitLeakSensorImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater);

            case VALVE:
                return new HomekitValveImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater);

            case MOTION_SENSOR:
                return new HomekitMotionSensorImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater);

            case LIGHTBULB:
                return new HomekitLightbulbImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater);

            case DIMMABLE_LIGHTBULB:
                return new HomekitDimmableLightbulbImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater);

            case COLORFUL_LIGHTBULB:
                return new HomekitColorfulLightbulbImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater);

            case THERMOSTAT:
                return new HomekitThermostatImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater, settings);

            case SWITCH:
                return new HomekitSwitchImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater);

            case TEMPERATURE_SENSOR:
                return new HomekitTemperatureSensorImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater,
                        settings);

            case HUMIDITY_SENSOR:
                return new HomekitHumiditySensorImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater);
            case BLINDS:
            case WINDOW_COVERING:
                return new HomekitWindowCoveringImpl(taggedItem, itemRegistry, itemChannelLinkRegistry, updater);
        }

        throw new Exception("Unknown homekit type: " + taggedItem.getDeviceType());
    }
}
