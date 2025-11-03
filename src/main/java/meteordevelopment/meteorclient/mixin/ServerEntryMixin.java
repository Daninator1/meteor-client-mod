/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.ISyncedServerInfo;
import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import meteordevelopment.meteorclient.systems.friends.ServerSync;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class ServerEntryMixin {

    @Shadow
    @Nullable
    private Identifier statusIconTexture;
    @Shadow
    @Final
    private ServerInfo server;
    @Shadow
    @Final
    private MultiplayerScreen screen;
    private int indexModifier;

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;Lnet/minecraft/client/network/ServerInfo;)V")
    private void init(MultiplayerServerListWidget multiplayerServerListWidget, MultiplayerScreen screen, ServerInfo server, CallbackInfo ci) {
        this.indexModifier = GetIndexModifier();
    }

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

    @Redirect(
        method = "render(Lnet/minecraft/client/gui/DrawContext;IIZF)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I"
        )
    )
    private int renderRedirectIndexOf(List<?> list, Object obj) {
        return list.indexOf(obj) - this.indexModifier;
    }

    @Redirect(
        method = "mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I"
        )
    )
    private int mouseClickedRedirectIndexOf(List<?> list, Object obj) {
        return list.indexOf(obj) - this.indexModifier;
    }

    @Redirect(
        method = "keyPressed(Lnet/minecraft/client/input/KeyInput;)Z",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I"
        )
    )
    private int keyPressedRedirectIndexOf(List<?> list, Object obj) {
        return list.indexOf(obj) - this.indexModifier;
    }

    /**
     * @author me
     * @reason because I get a warning otherwise
     */
    @Overwrite
    private void swapEntries(int i, int j) {
        this.screen.getServerList().swapEntries(i, j);
        var serverListWidget = ((MultiplayerScreenAccessor) this.screen).meteor$getServerListWidget();
        var entryListWidgetInvoker = (EntryListWidgetInvoker) serverListWidget;
        entryListWidgetInvoker.meteor$swapEntriesOnPositions(i + this.indexModifier, j + this.indexModifier);
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIZF)V", at = @At("TAIL"))
    private void onRenderTail(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks, CallbackInfo ci) {
        if (!ServerSync.get().enabled) return;

        var thisServerEntry = ((MultiplayerServerListWidget.ServerEntry)(Object)this);

        if (((ISyncedServerInfo) this.server).getId() != null) {
            int i = thisServerEntry.getContentX() + thisServerEntry.getContentWidth() - 10 - 5;
            int yOffset = 10;
            int width = 10;
            int height = 10;

            context.drawTexture(RenderPipelines.GUI_TEXTURED, MeteorClient.identifier("textures/cloud.png"), i, thisServerEntry.getY() + yOffset, 0, 0, width, height, width, height, width, height);

            if (mouseX >= i && mouseX <= i + width && mouseY >= thisServerEntry.getY() + yOffset && mouseY <= thisServerEntry.getY() + height + yOffset) {
                context.drawTooltip(Text.literal("Server synced"), mouseX, mouseY);
            }
        }
    }

    private int GetIndexModifier() {
        if (!PlayStatus.get().enabled) return 0;

        var playStatusEntries = PlayStatus.get().fetchPlayStatusEntries();
        if (playStatusEntries == null || playStatusEntries.length == 0) return 0;
        return playStatusEntries.length + 1;
    }
}


