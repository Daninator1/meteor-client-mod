/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.ISyncedServerData;
import meteordevelopment.meteorclient.systems.friends.ServerSync;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public class OnlineServerEntryMixin {

    //    @Shadow
//    @Nullable
//    private Identifier statusIconTexture;
//    @Shadow
//    @Final
//    private ServerInfo server;
//    @Shadow
//    @Final
//    private MultiplayerScreen screen;
    @Shadow
    @Final
    private ServerData serverData;
//    @Shadow
//    @Final
//    private Minecraft minecraft;
//    private int indexModifier;

//    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;Lnet/minecraft/client/network/ServerInfo;)V")
//    private void init(MultiplayerServerListWidget multiplayerServerListWidget, MultiplayerScreen screen, ServerInfo server, CallbackInfo ci) {
//        this.indexModifier = GetIndexModifier();
//    }

//    @ModifyVariable(method = "render(Lnet/minecraft/client/gui/DrawContext;IIZF)V", at = @At("HEAD"), ordinal = 0)
//    private int onRenderHead(int index) {
//        return index - this.indexModifier;
//    }

//    @ModifyVariable(
//        method = "render(Lnet/minecraft/client/gui/DrawContext;IIZF)V", // <-- replace with the real method name
//        at = @At(
//            value = "INVOKE",
//            target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I",
//            shift = At.Shift.AFTER
//        ),
//        ordinal = 0 // usually the first int assigned after indexOf
//    )
//    private int modifyIndex(int i) {
//        // i is the original index from indexOf
//        // modify it however you want
//        return i - this.indexModifier;
//    }

//    @Redirect(
//        method = "render(Lnet/minecraft/client/gui/DrawContext;IIZF)V",
//        at = @At(
//            value = "INVOKE",
//            target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I"
//        )
//    )
//    private int renderRedirectIndexOf(List<?> list, Object obj) {
//        return list.indexOf(obj) - this.indexModifier;
//    }
//
//    @Redirect(
//        method = "mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z",
//        at = @At(
//            value = "INVOKE",
//            target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I"
//        )
//    )
//    private int mouseClickedRedirectIndexOf(List<?> list, Object obj) {
//        return list.indexOf(obj) - this.indexModifier;
//    }
//
//    @Redirect(
//        method = "keyPressed(Lnet/minecraft/client/input/KeyInput;)Z",
//        at = @At(
//            value = "INVOKE",
//            target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I"
//        )
//    )
//    private int keyPressedRedirectIndexOf(List<?> list, Object obj) {
//        return list.indexOf(obj) - this.indexModifier;
//    }

//    /**
//     * @author me
//     * @reason because I get a warning otherwise
//     */
//    @Overwrite
//    private void swapEntries(int i, int j) {
//        this.screen.getServerList().swapEntries(i, j);
//        var serverListWidget = ((MultiplayerScreenAccessor) this.screen).meteor$getServerListWidget();
//        var entryListWidgetInvoker = (EntryListWidgetInvoker) serverListWidget;
//        entryListWidgetInvoker.meteor$swapEntriesOnPositions(i + this.indexModifier, j + this.indexModifier);
//    }

    @Inject(method = "extractContent", at = @At("TAIL"))
    private void onExtractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a, CallbackInfo ci) {
        if (!ServerSync.get().enabled) return;

        var thisServerEntry = ((ServerSelectionList.OnlineServerEntry) (Object) this);

        if (((ISyncedServerData) this.serverData).meteor$getId() != null) {
            int i = thisServerEntry.getContentX() + thisServerEntry.getContentWidth() - 10 - 5;
            int yOffset = 10;
            int width = 10;
            int height = 10;

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, MeteorClient.identifier("textures/cloud.png"), i, thisServerEntry.getY() + yOffset, width, height);

            if (mouseX >= i && mouseX <= i + width && mouseY >= thisServerEntry.getY() + yOffset && mouseY <= thisServerEntry.getY() + height + yOffset) {
                graphics.setTooltipForNextFrame(Component.literal("Server synced"), mouseX, mouseY);
            }
        }
    }

//    private int GetIndexModifier() {
//        if (!PlayStatus.get().enabled) return 0;
//
//        var playStatusEntries = PlayStatus.get().fetchPlayStatusEntries();
//        if (playStatusEntries == null || playStatusEntries.length == 0) return 0;
//        return playStatusEntries.length + 1;
//    }
}


