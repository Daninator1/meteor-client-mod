/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.ISyncedServerData;
import meteordevelopment.meteorclient.systems.friends.ServerSync;
import meteordevelopment.meteorclient.utils.misc.PlayStatusSeparatorEntry;
import meteordevelopment.meteorclient.utils.misc.PlayStatusServerEntry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public class OnlineServerEntryMixin {
    @Shadow
    @Final
    private ServerData serverData;

    @Shadow
    @Final
    private JoinMultiplayerScreen screen;

    @ModifyExpressionValue(
        method = "extractContent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/multiplayer/ServerSelectionList;children()Ljava/util/List;"
        )
    )
    private List<ServerSelectionList.Entry> modifyExtractContentChildren(List<ServerSelectionList.Entry> children) {
        return children.stream()
            .filter(entry -> !(entry instanceof PlayStatusSeparatorEntry))
            .filter(entry -> !(entry instanceof PlayStatusServerEntry))
            .toList();
    }

    @ModifyExpressionValue(
        method = "mouseClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/multiplayer/ServerSelectionList;children()Ljava/util/List;"
        )
    )
    private List<ServerSelectionList.Entry> modifyMouseClickedChildren(List<ServerSelectionList.Entry> children) {
        return children.stream()
            .filter(entry -> !(entry instanceof PlayStatusSeparatorEntry))
            .filter(entry -> !(entry instanceof PlayStatusServerEntry))
            .toList();
    }

    @ModifyExpressionValue(
        method = "keyPressed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/multiplayer/ServerSelectionList;children()Ljava/util/List;"
        )
    )
    private List<ServerSelectionList.Entry> modifyKeyPressedChildren(List<ServerSelectionList.Entry> children) {
        return children.stream()
            .filter(entry -> !(entry instanceof PlayStatusSeparatorEntry))
            .filter(entry -> !(entry instanceof PlayStatusServerEntry))
            .toList();
    }

    @Inject(method = "swap(II)V", at = @At("HEAD"), cancellable = true)
    private void onSwap(int currentIndex, int newIndex, CallbackInfo ci) {
        this.screen.getServers().swap(currentIndex, newIndex);

        ServerSelectionList serverSelectionList = ((JoinMultiplayerScreenAccessor) this.screen).meteor$getServerSelectionList();

        int offset = (int) serverSelectionList.children().stream()
            .filter(entry -> entry instanceof PlayStatusSeparatorEntry || entry instanceof PlayStatusServerEntry)
            .count();

        ((AbstractSelectionListAccessor) serverSelectionList).meteor$swap(currentIndex + offset, newIndex + offset);

        ci.cancel();
    }

    @Inject(method = "extractContent", at = @At("TAIL"))
    private void onExtractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a, CallbackInfo ci) {
        if (!ServerSync.get().enabled) return;

        var thisServerEntry = ((ServerSelectionList.OnlineServerEntry) (Object) this);

        if (((ISyncedServerData) this.serverData).meteor$getId() != null) {
            int i = thisServerEntry.getContentX() + thisServerEntry.getContentWidth() - 10 - 5;
            int yOffset = 10;
            int width = 10;
            int height = 10;

            graphics.blit(RenderPipelines.GUI_TEXTURED, MeteorClient.identifier("textures/cloud.png"), i, thisServerEntry.getY() + yOffset, width, height, width, height, width, height);

            if (mouseX >= i && mouseX <= i + width && mouseY >= thisServerEntry.getY() + yOffset && mouseY <= thisServerEntry.getY() + height + yOffset) {
                graphics.setTooltipForNextFrame(Component.literal("Server synced"), mouseX, mouseY);
            }
        }
    }
}


