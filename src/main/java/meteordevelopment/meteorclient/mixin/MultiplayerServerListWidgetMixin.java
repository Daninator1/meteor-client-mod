/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.utils.misc.FriendServer;
import meteordevelopment.meteorclient.utils.misc.FriendServerEntry;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.class)
public class MultiplayerServerListWidgetMixin extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
    @Shadow
    @Final
    private MultiplayerScreen screen;

    public MultiplayerServerListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Inject(method = "updateEntries", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;servers:Ljava/util/List;"))
    private void onUpdateEntries(CallbackInfo callbackInfo) {
        FriendServer[] friendServers = Http.get("http://localhost:5000/friendServers").sendJson(FriendServer[].class);

        for (FriendServer friendServer : friendServers) {
            var entry = new FriendServerEntry(this.screen, new LanServerInfo(friendServer.playerName, friendServer.getServerAddress()), friendServer.friendName);
            this.addEntry(entry);
        }
    }
}


