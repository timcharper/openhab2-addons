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

import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.openhab.io.homekit.internal.HomekitAccessoryUpdater;
import org.openhab.io.homekit.internal.HomekitTaggedItem;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.Valve;
import com.beowulfe.hap.accessories.properties.ValveType;

/**
 *
 * @author Tim Harper - Initial implementation
 */
public class HomekitValveImpl extends AbstractHomekitAccessoryImpl<SwitchItem> implements Valve {
    public HomekitValveImpl(HomekitTaggedItem taggedItem, ItemRegistry itemRegistry, HomekitAccessoryUpdater updater) {
        super(taggedItem, itemRegistry, updater, SwitchItem.class);
    }

    @Override
    public CompletableFuture<Boolean> getValveActive() {
        OnOffType state = getItem().getStateAs(OnOffType.class);
        if (state == null) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(state == OnOffType.ON);
    }

    @Override
    public CompletableFuture<Void> setValveActive(boolean state) throws Exception {
        GenericItem item = getItem();
        if (item instanceof SwitchItem) {
            ((SwitchItem) item).send(state ? OnOffType.ON : OnOffType.OFF);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeValveActive(HomekitCharacteristicChangeCallback callback) {
        getUpdater().subscribe(getItem(), callback);
    }

    @Override
    public void unsubscribeValveActive() {
        getUpdater().unsubscribe(getItem());
    }

    @Override
    public CompletableFuture<Boolean> getValveInUse() {
        return getValveActive();
    }

    @Override
    public void subscribeValveInUse(HomekitCharacteristicChangeCallback callback) {
        subscribeValveActive(callback);
    }

    @Override
    public void unsubscribeValveInUse() {
        // we're unsubscribing twice but this is probably fine.
        unsubscribeValveActive();
    }

    @Override
    public CompletableFuture<ValveType> getValveType() {
        // TODO - make this configurable; possibly via additional tags? ValveType:Generic etc
        return CompletableFuture.completedFuture(ValveType.GENERIC);
    }

    @Override
    public void subscribeValveType(HomekitCharacteristicChangeCallback callback) {
        // nothing changes here
    }

    @Override
    public void unsubscribeValveType() {
        // nothing changes here
    }
}
