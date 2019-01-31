/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.homekit.internal.accessories;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.openhab.io.homekit.internal.HomekitAccessoryType;
import org.openhab.io.homekit.internal.HomekitAccessoryUpdater;
import org.openhab.io.homekit.internal.HomekitCharacteristicType;
import org.openhab.io.homekit.internal.HomekitSettings;
import org.openhab.io.homekit.internal.HomekitTaggedItem;
import org.openhab.io.homekit.internal.battery.BatteryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beowulfe.hap.HomekitAccessory;
import com.google.common.collect.ImmutableMap;

/**
 * Creates a HomekitAccessory for a given HomekitTaggedItem.
 *
 * @author Andy Lintner
 */
public class HomekitAccessoryFactory {
    static Logger logger = LoggerFactory.getLogger(HomekitTaggedItem.class);

    public static HomekitAccessory create(HomekitTaggedItem taggedItem, ItemRegistry itemRegistry,
            HomekitAccessoryUpdater updater, HomekitSettings settings) throws Exception {
        logger.debug("Constructing {} of accessoryType {}", taggedItem.getName(), taggedItem.getAccessoryType());

        Map<HomekitCharacteristicType, Item> characteristicItems = getCharacteristicItems(taggedItem);

        switch (taggedItem.getAccessoryType()) {
            case LEAK_SENSOR:
                HomekitTaggedItem leakSensorAccessory = getPrimaryAccessory(taggedItem,
                        HomekitAccessoryType.LEAK_SENSOR, itemRegistry).orElseThrow(
                                () -> new Exception("Leak accessory group should have a leak sensor in it"));

                return new HomekitLeakSensorImpl(leakSensorAccessory, itemRegistry, updater,
                        BatteryStatus.getFromCharacteristics(characteristicItems));

            case VALVE:
                return new HomekitValveImpl(taggedItem, itemRegistry, updater);

            case MOTION_SENSOR:
                HomekitTaggedItem motionSensorAccessory = getPrimaryAccessory(taggedItem,
                        HomekitAccessoryType.MOTION_SENSOR, itemRegistry)
                                .orElseThrow(() -> new Exception(
                                        "Motion sensor accessory group should have a motion sensor item in it"));

                return new HomekitMotionSensorImpl(motionSensorAccessory, itemRegistry, updater,
                        BatteryStatus.getFromCharacteristics(characteristicItems));

            case OCCUPANCY_SENSOR:
                HomekitTaggedItem occupancySensorAccessory = getPrimaryAccessory(taggedItem,
                        HomekitAccessoryType.OCCUPANCY_SENSOR, itemRegistry)
                                .orElseThrow(() -> new Exception(
                                        "Occupancy sensor accessory group should have a occupancy sensor item in it"));

                return new HomekitOccupancySensorImpl(occupancySensorAccessory, itemRegistry, updater,
                        BatteryStatus.getFromCharacteristics(characteristicItems));

            case CONTACT_SENSOR:
                HomekitTaggedItem contactSensorAccessory = getPrimaryAccessory(taggedItem,
                        HomekitAccessoryType.CONTACT_SENSOR, itemRegistry)
                                .orElseThrow(() -> new Exception(
                                        "Contact sensor accessory group should have a occupancy sensor item in it"));

                return new HomekitContactSensorImpl(contactSensorAccessory, itemRegistry, updater,
                        BatteryStatus.getFromCharacteristics(characteristicItems));

            case LIGHTBULB:
                return new HomekitLightbulbImpl(taggedItem, itemRegistry, updater);

            case DIMMABLE_LIGHTBULB:
                return new HomekitDimmableLightbulbImpl(taggedItem, itemRegistry, updater);

            case COLORFUL_LIGHTBULB:
                return new HomekitColorfulLightbulbImpl(taggedItem, itemRegistry, updater);

            case THERMOSTAT:
                HomekitTaggedItem temperatureAccessory = getPrimaryAccessory(taggedItem,
                        HomekitAccessoryType.TEMPERATURE_SENSOR, itemRegistry)
                                .orElseThrow(() -> new Exception("Thermostats need a CurrentTemperature accessory"));

                return new HomekitThermostatImpl(taggedItem, itemRegistry, updater, settings,
                        temperatureAccessory.getItem(), getCharacteristicItems(taggedItem));

            case SWITCH:
                return new HomekitSwitchImpl(taggedItem, itemRegistry, updater);

            case TEMPERATURE_SENSOR:
                return new HomekitTemperatureSensorImpl(taggedItem, itemRegistry, updater, settings);

            case HUMIDITY_SENSOR:
                return new HomekitHumiditySensorImpl(taggedItem, itemRegistry, updater);
            case BLINDS:
            case WINDOW_COVERING:
                return new HomekitWindowCoveringImpl(taggedItem, itemRegistry, updater);
            case SMOKE_SENSOR:
                HomekitTaggedItem smokeSensorAccessory = getPrimaryAccessory(taggedItem,
                        HomekitAccessoryType.SMOKE_SENSOR, itemRegistry).orElseThrow(
                                () -> new Exception("Smoke accessory group should have a smoke sensor in it"));

                return new HomekitSmokeSensorImpl(smokeSensorAccessory, itemRegistry, updater,
                        BatteryStatus.getFromCharacteristics(characteristicItems));
            case CARBON_MONOXIDE_SENSOR:
                HomekitTaggedItem carbonMonoxideSensorAccessory = getPrimaryAccessory(taggedItem,
                        HomekitAccessoryType.CARBON_MONOXIDE_SENSOR, itemRegistry)
                                .orElseThrow(() -> new Exception(
                                        "Carbon monoxide accessory group should have a carbon monoxide sensor in it"));

                return new HomekitSmokeSensorImpl(carbonMonoxideSensorAccessory, itemRegistry, updater,
                        BatteryStatus.getFromCharacteristics(characteristicItems));

        }

        throw new Exception("Unknown homekit type: " + taggedItem.getAccessoryType());
    }

    /**
     * Given an accessory group, return the item in the group tagged as an accessory.
     *
     * @param taggedItem The group item containing our item, or, the accessory item.
     * @param accessoryType The accessory type for which we're looking
     * @return
     */
    private static Optional<HomekitTaggedItem> getPrimaryAccessory(HomekitTaggedItem taggedItem,
            HomekitAccessoryType accessoryType, ItemRegistry itemRegistry) {
        logger.info("{} isGroup? {}", taggedItem.getName(), taggedItem.isGroup(),
                taggedItem.isMemberOfAccessoryGroup());
        if (taggedItem.isGroup()) {
            GroupItem groupItem = (GroupItem) taggedItem.getItem();
            return groupItem.getMembers().stream().filter(item -> item.hasTag(accessoryType.getTag())).findFirst()
                    .map(item -> new HomekitTaggedItem(item, itemRegistry));
        } else if (taggedItem.getAccessoryType() == accessoryType) {
            return Optional.of(taggedItem);
        } else {
            return Optional.empty();
        }
    }

    private static Map<HomekitCharacteristicType, Item> getCharacteristicItems(HomekitTaggedItem taggedItem) {
        if (taggedItem.isGroup()) {
            ImmutableMap.Builder<HomekitCharacteristicType, Item> builder = new ImmutableMap.Builder<>();
            GroupItem groupItem = (GroupItem) taggedItem.getItem();
            groupItem.getMembers().stream().forEach(item -> {
                HomekitCharacteristicType itemType = HomekitTaggedItem.findCharacteristicType(item);
                if (itemType != null) {
                    builder.put(itemType, item);
                }
            });
            return builder.build();
        } else {
            // do nothing; only accessory groups have characteristic items
            return Collections.emptyMap();
        }
    }
}
