/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.homekit.internal.accessories;

import org.openhab.io.homekit.internal.HomekitCharacteristicType;

/**
 *
 * @author Tim Harper - Initial contribution
 */
public class IncompleteAccessoryException extends Exception {
    final HomekitCharacteristicType missingType;

    public IncompleteAccessoryException(HomekitCharacteristicType missingType) {
        super(String.format("Missing accessory type %s", missingType.getTag()));
        this.missingType = missingType;
    }
}
