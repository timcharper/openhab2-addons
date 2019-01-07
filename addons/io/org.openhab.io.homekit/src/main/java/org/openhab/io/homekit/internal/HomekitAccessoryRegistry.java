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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beowulfe.hap.HomekitAccessory;
import com.beowulfe.hap.HomekitRoot;

/**
 * Stores the created HomekitAccessories. GroupedAccessories are also held here
 * in a pre-created pending state until all required characteristics are found.
 *
 * @author Andy Lintner
 */
class HomekitAccessoryRegistry {

    private HomekitRoot bridge;
    private final Map<String, HomekitAccessory> createdAccessories = new HashMap<>();
    private final Set<Integer> createdIds = new HashSet<>();

    private final Logger logger = LoggerFactory.getLogger(HomekitAccessoryRegistry.class);

    public synchronized void remove(HomekitTaggedItem taggedItem) {
        if (createdAccessories.containsKey(taggedItem.getName())) {
            HomekitAccessory accessory = createdAccessories.remove(taggedItem.getName());
            logger.debug("Removed accessory {}", accessory.getId());
            bridge.removeAccessory(accessory);
        }
    }

    public synchronized void clear() {
        while (!createdAccessories.isEmpty()) {
            bridge.removeAccessory(createdAccessories.remove(0));
        }
        createdIds.clear();
    }

    public synchronized void setBridge(HomekitRoot bridge) {
        this.bridge = bridge;
        createdAccessories.values().forEach(accessory -> bridge.addAccessory(accessory));
    }

    public synchronized void addRootDevice(String itemName, HomekitAccessory accessory) {
        createdAccessories.put(itemName, accessory);
        createdIds.add(accessory.getId());
        if (bridge != null) {
            bridge.addAccessory(accessory);
        }
        logger.debug("Added accessory {}", accessory.getId());
    }
}
