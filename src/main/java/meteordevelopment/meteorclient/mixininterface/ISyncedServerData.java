/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import java.util.UUID;

public interface ISyncedServerData {
    UUID meteor$getId();
    void meteor$setId(UUID id);
}
