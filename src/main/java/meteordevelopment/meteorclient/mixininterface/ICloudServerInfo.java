/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import java.util.UUID;

public interface ICloudServerInfo {
    UUID getCloudId();
    void setCloudId(UUID cloudId);
}
