/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.homekit.internal.battery;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.openhab.io.homekit.internal.HomekitAccessoryUpdater;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;

/**
 *
 * @author Tim Harper - Initial contribution
 */
@NonNullByDefault
public class BatteryLevelStatus implements BatteryStatus {

    private NumberItem batteryLevelItem;

    BatteryLevelStatus(NumberItem batteryLevelItem) {
        this.batteryLevelItem = batteryLevelItem;
    }

    @Override
    public @Nullable Boolean isLow() {
        DecimalType level = batteryLevelItem.getStateAs(DecimalType.class);

        if (level == null) {
            return null;
        } else {
            return level.intValue() < 10;
        }
    }

    @Override
    public void subscribe(HomekitAccessoryUpdater updater, HomekitCharacteristicChangeCallback callback) {
        updater.subscribe(batteryLevelItem, callback);
    }

    @Override
    public void unsubscribe(HomekitAccessoryUpdater updater) {
        updater.unsubscribe(batteryLevelItem);
    }
}