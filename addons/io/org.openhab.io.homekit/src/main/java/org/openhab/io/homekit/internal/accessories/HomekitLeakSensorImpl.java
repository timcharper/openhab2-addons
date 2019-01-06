/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.homekit.internal.accessories;

import java.util.concurrent.CompletableFuture;

import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.link.ItemChannelLinkRegistry;
import org.openhab.io.homekit.internal.HomekitAccessoryUpdater;
import org.openhab.io.homekit.internal.HomekitTaggedItem;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.LeakSensor;

/**
 *
 * @author Tim Harper - Initial implementation
 */
public class HomekitLeakSensorImpl extends AbstractHomekitAccessoryImpl<SwitchItem> implements LeakSensor {
    public HomekitLeakSensorImpl(HomekitTaggedItem taggedItem, ItemRegistry itemRegistry,
            ItemChannelLinkRegistry itemChannelLinkRegistry, HomekitAccessoryUpdater updater) {
        super(taggedItem, itemRegistry, itemChannelLinkRegistry, updater, SwitchItem.class);
    }

    @Override
    public CompletableFuture<Boolean> getLeakDetected() {
        OnOffType state = getItem().getStateAs(OnOffType.class);
        if (!isOnline()) {
            // Report device offline
            return CompletableFuture.completedFuture(null);
        } else if (state == null) {
            return CompletableFuture.completedFuture(false);
        } else {
            return CompletableFuture.completedFuture(state == OnOffType.ON);
        }
    }

    @Override
    public void subscribeLeakDetected(HomekitCharacteristicChangeCallback callback) {
        getUpdater().subscribe(getItem(), callback);
    }

    @Override
    public void unsubscribeLeakDetected() {
        getUpdater().unsubscribe(getItem());
    }
}
