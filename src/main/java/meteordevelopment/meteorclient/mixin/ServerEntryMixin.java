/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class ServerEntryMixin {

    private int indexModifier;

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;Lnet/minecraft/client/network/ServerInfo;)V")
    private void init(MultiplayerServerListWidget multiplayerServerListWidget, MultiplayerScreen screen, ServerInfo server, CallbackInfo ci) {
        this.indexModifier = GetIndexModifier();
    }

    @ModifyVariable(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIIIIIIZF)V", at = @At("HEAD"), ordinal = 0)
    private int onRender(int index) {
        return index - this.indexModifier;
    }

    @Redirect(method = "keyPressed(III)Z", at = @At(value = "INVOKE", target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I", ordinal = 0))
    private int onKeyPressed(List<MultiplayerServerListWidget.Entry> children, Object object) {
        return children.indexOf(object) - this.indexModifier;
    }

    @Inject(method = "keyPressed(III)Z", at = @At(value = "INVOKE", target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void OnKeyPressed2(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir, MultiplayerServerListWidget multiplayerServerListWidget, int i) {
        if (i < 0) cir.setReturnValue(true);
    }

    @Redirect(method = "mouseClicked(DDI)Z", at = @At(value = "INVOKE", target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I", ordinal = 1))
    private int onMouseClicked(List<MultiplayerServerListWidget.Entry> children, Object object) {
        return children.indexOf(object) - this.indexModifier;
    }

    @ModifyArg(method = "swapEntries(II)V", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), index = 0)
    private int onSwapEntries(int j) {
        return j + this.indexModifier;
    }

    private int GetIndexModifier() {
        if (!PlayStatus.get().enabled) return 0;

        var playStatusEntries = PlayStatus.get().fetchPlayStatusEntries();
        if (playStatusEntries == null || playStatusEntries.length == 0) return 0;
        return playStatusEntries.length + 1;
    }
}


