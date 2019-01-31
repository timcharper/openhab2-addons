/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.homekit.internal.battery;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.openhab.io.homekit.internal.HomekitAccessoryUpdater;
import org.openhab.io.homekit.internal.HomekitCharacteristicType;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;

/**
 *
 * @author Tim Harper - Initial contribution
 */
@NonNullByDefault
public interface BatteryStatus {

    @Nullable
    Boolean isLow();

    void subscribe(HomekitAccessoryUpdater updater, HomekitCharacteristicChangeCallback callback);

    public void unsubscribe(HomekitAccessoryUpdater updater);

    static BatteryStatus getFromCharacteristics(Map<HomekitCharacteristicType, Item> characteristicItems) {
        if (characteristicItems.containsKey(HomekitCharacteristicType.BATTERY_LEVEL)) {
            return new BatteryLevelStatus(
                    (NumberItem) characteristicItems.get(HomekitCharacteristicType.BATTERY_LEVEL));
        } else if (characteristicItems.containsKey(HomekitCharacteristicType.BATTERY_LOW_STATUS)) {
            return new BatteryLowStatus(
                    (SwitchItem) characteristicItems.get(HomekitCharacteristicType.BATTERY_LOW_STATUS));
        } else {
            return new NoBatteryStatus();
        }
    }
}