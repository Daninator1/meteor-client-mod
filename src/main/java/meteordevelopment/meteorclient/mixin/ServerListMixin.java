/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IServerListAdditionalMethods;
import meteordevelopment.meteorclient.mixininterface.ISyncedServerData;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Mixin(ServerList.class)
public class ServerListMixin implements IServerListAdditionalMethods {
    @Shadow
    @Final
    private List<ServerData> serverList;

    @Shadow
    @Final
    private List<ServerData> hiddenServerList;

    @Override
    public @Nullable ServerData meteor$get(UUID id) {
        for (ServerData serverData : this.serverList) {
            var serverId = ((ISyncedServerData) serverData).meteor$getId();
            if (serverId != null && serverId.equals(id)) {
                return serverData;
            }
        }

        for (ServerData serverData : this.hiddenServerList) {
            var serverId = ((ISyncedServerData) serverData).meteor$getId();
            if (serverId != null && serverId.equals(id)) {
                return serverData;
            }
        }

        return null;
    }

    @Override
    public Stream<ServerData> meteor$stream() {
        return Stream.concat(this.serverList.stream(), this.hiddenServerList.stream());
    }
}
