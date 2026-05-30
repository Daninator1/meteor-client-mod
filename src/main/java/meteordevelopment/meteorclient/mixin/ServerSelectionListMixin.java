/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import meteordevelopment.meteorclient.utils.misc.PlayStatusSeparatorEntry;
import meteordevelopment.meteorclient.utils.misc.PlayStatusServerEntry;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.server.LanServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(ServerSelectionList.class)
public class ServerSelectionListMixin extends ObjectSelectionList<ServerSelectionList.Entry> {
    @Shadow
    @Final
    private JoinMultiplayerScreen screen;

    public ServerSelectionListMixin(final JoinMultiplayerScreen screen, final Minecraft minecraft, final int width, final int height, final int y, final int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Inject(method = "refreshEntries", at = @At("HEAD"))
    private void onRefreshEntries(CallbackInfo callbackInfo) {
        if (!PlayStatus.get().enabled) return;

        MeteorExecutor.execute(() -> {
            var playStatusEntries = PlayStatus.get().fetchPlayStatusEntries();
            if (playStatusEntries.length == 0) return;

            var separatorEntry = new PlayStatusSeparatorEntry();
            synchronized (this) {
                this.addEntryToTop(separatorEntry);
            }

            for (int i = playStatusEntries.length - 1; i >= 0; i--) {
                if (playStatusEntries[i].playerName.equals(mc.player != null ? mc.player.getName().getString() : null))
                    continue;

                var entry = new PlayStatusServerEntry(this.screen, new LanServer(playStatusEntries[i].playerName, playStatusEntries[i].server), playStatusEntries[i].name);
                synchronized (this) {
                    this.addEntryToTop(entry);
                }
            }
        });
    }
}


