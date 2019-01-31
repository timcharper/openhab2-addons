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
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.openhab.io.homekit.internal.HomekitAccessoryUpdater;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;

/**
 *
 * @author Tim Harper - Initial contribution
 */
@NonNullByDefault
public class BatteryLowStatus implements BatteryStatus {

    private SwitchItem batterySwitchItem;

    BatteryLowStatus(SwitchItem batterySwitchItem) {
        this.batterySwitchItem = batterySwitchItem;
    }

    @Override
    @Nullable
    public Boolean isLow() {
        OnOffType state = batterySwitchItem.getStateAs(OnOffType.class);
        if (state == null) {
            return null;
        } else {
            return state == OnOffType.ON;
        }
    }

    @Override
    public void subscribe(HomekitAccessoryUpdater updater, HomekitCharacteristicChangeCallback callback) {
        updater.subscribe(batterySwitchItem, callback);
    }

    @Override
    public void unsubscribe(HomekitAccessoryUpdater updater) {
        updater.unsubscribe(batterySwitchItem);
    }
}
