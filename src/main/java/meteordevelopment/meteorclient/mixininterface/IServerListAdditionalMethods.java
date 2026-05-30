/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import net.minecraft.client.multiplayer.ServerData;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

public interface IServerListAdditionalMethods {
    @Nullable
    ServerData meteor$get(UUID id);

    Stream<ServerData> meteor$stream();
}
