/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import meteordevelopment.meteorclient.utils.misc.PlayStatusEntry;
import meteordevelopment.meteorclient.utils.misc.PlayStatusSeparatorEntry;
import meteordevelopment.meteorclient.utils.misc.PlayStatusServerEntry;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(MultiplayerServerListWidget.class)
public class MultiplayerServerListWidgetMixin extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
    @Shadow
    @Final
    private MultiplayerScreen screen;

    public MultiplayerServerListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
    }

    @Inject(method = "updateEntries", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;servers:Ljava/util/List;"))
    private void onUpdateEntries(CallbackInfo callbackInfo) {
        if (!PlayStatus.get().enabled) return;

        MeteorExecutor.execute(() -> {
            var separatorEntry = new PlayStatusSeparatorEntry();
            synchronized (this) {
                this.addEntryToTop(separatorEntry);
            }

            var playStatusEntries = PlayStatus.get().fetchPlayStatusEntries();
            if (playStatusEntries == null) return;

            for (int i = playStatusEntries.length - 1; i >= 0; i--) {
                if (playStatusEntries[i].playerName.equals(mc.player != null ? mc.player.getName().getString() : null))
                    continue;

                var entry = new PlayStatusServerEntry(this.screen, new LanServerInfo(playStatusEntries[i].playerName, playStatusEntries[i].server), playStatusEntries[i].name);
                synchronized (this) {
                    this.addEntryToTop(entry);
                }
            }
        });
    }
}


