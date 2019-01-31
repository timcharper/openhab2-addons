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
import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.library.items.DimmerItem;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.io.homekit.internal.HomekitAccessoryUpdater;
import org.openhab.io.homekit.internal.HomekitTaggedItem;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.Fan;
import com.beowulfe.hap.accessories.properties.RotationDirection;

/**
 * Implements DimmableLightBulb using an Item that provides a On/Off and Percent state.
 *
 * @author Cody Cutrer
 */
class HomekitFanImpl extends AbstractHomekitAccessoryImpl<GenericItem> implements Fan {

    public HomekitFanImpl(HomekitTaggedItem taggedItem, ItemRegistry itemRegistry, HomekitAccessoryUpdater updater) {
        super(taggedItem, itemRegistry, updater, GenericItem.class);
    }

    @Override
    public CompletableFuture<Boolean> getFanPower() {
        OnOffType state = getItem().getStateAs(OnOffType.class);
        return CompletableFuture.completedFuture(state == OnOffType.ON);
    }

    @Override
    public CompletableFuture<Void> setFanPower(boolean value) throws Exception {
        GenericItem item = getItem();
        if (item instanceof SwitchItem) {
            ((SwitchItem) item).send(value ? OnOffType.ON : OnOffType.OFF);
        } else if (item instanceof GroupItem) {
            ((GroupItem) item).send(value ? OnOffType.ON : OnOffType.OFF);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeFanPower(HomekitCharacteristicChangeCallback callback) {
        getUpdater().subscribe(getItem(), callback);
    }

    @Override
    public void unsubscribeFanPower() {
        getUpdater().unsubscribe(getItem());
    }

    @Override
    public CompletableFuture<Integer> getRotationSpeed() {
        State state = getItem().getStateAs(PercentType.class);
        if (state instanceof PercentType) {
            PercentType speed = (PercentType) state;
            return CompletableFuture.completedFuture(speed.intValue());
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public CompletableFuture<Void> setRotationSpeed(Integer value) throws Exception {
        GenericItem item = getItem();
        if (item instanceof DimmerItem) {
            ((DimmerItem) item).send(new PercentType(value));
        } else if (item instanceof GroupItem) {
            ((GroupItem) item).send(new PercentType(value));
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeRotationSpeed(HomekitCharacteristicChangeCallback callback) {
        getUpdater().subscribe(getItem(), "speed", callback);
    }

    @Override
    public void unsubscribeRotationSpeed() {
        getUpdater().unsubscribe(getItem(), "speed");
    }

    @Override
    public CompletableFuture<RotationDirection> getRotationDirection() {
        return CompletableFuture.completedFuture(RotationDirection.CLOCKWISE);
    }

    @Override
    public CompletableFuture<Void> setRotationDirection(RotationDirection direction) throws Exception {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeRotationDirection(HomekitCharacteristicChangeCallback callback) {
        getUpdater().subscribe(getItem(), "rotation", callback);
    }

    @Override
    public void unsubscribeRotationDirection() {
        getUpdater().unsubscribe(getItem(), "rotation");
    }
}
