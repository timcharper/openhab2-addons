/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.homekit.internal.battery;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.io.homekit.internal.HomekitAccessoryUpdater;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;

/**
 *
 * @author Tim Harper - Initial contribution
 */
public class NoBatteryStatus implements BatteryStatus {

    @Override
    public @Nullable Boolean isLow() {
        return false;
    }

    @Override
    public void subscribe(@NonNull HomekitAccessoryUpdater updater,
            @NonNull HomekitCharacteristicChangeCallback callback) {
        // do nothing
    }

    @Override
    public void unsubscribe(@NonNull HomekitAccessoryUpdater updater) {
        // do nothing
    }
}
