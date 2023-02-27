/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import meteordevelopment.meteorclient.utils.misc.PlayStatusEntry;
import meteordevelopment.meteorclient.utils.misc.PlayStatusSeparatorEntry;
import meteordevelopment.meteorclient.utils.misc.PlayStatusServerEntry;
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

import java.util.function.Predicate;

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
        if (!PlayStatus.get().enabled) return;

        var playStatusEntries = PlayStatus.get().getPlayStatusEntries();
        if (playStatusEntries == null || playStatusEntries.length == 0) return;

        for (PlayStatusEntry playStatusEntry : playStatusEntries) {
            var entry = new PlayStatusServerEntry(this.screen, new LanServerInfo(playStatusEntry.playerName, playStatusEntry.server), playStatusEntry.name);
            this.addEntry(entry);
        }

        var separatorEntry = new PlayStatusSeparatorEntry();
        this.addEntry(separatorEntry);
    }

    @ModifyArg(method = "moveSelection(Lnet/minecraft/client/gui/widget/EntryListWidget$MoveDirection;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;moveSelectionIf(Lnet/minecraft/client/gui/widget/EntryListWidget$MoveDirection;Ljava/util/function/Predicate;)Z"), index = 1)
    private Predicate<MultiplayerServerListWidget.Entry> onMoveSelection(Predicate<MultiplayerServerListWidget.Entry> predicate) {
        return (entry) -> predicate.test(entry) && !(entry instanceof PlayStatusSeparatorEntry);
    }
}


