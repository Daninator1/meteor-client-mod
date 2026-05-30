/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectJoinServerScreen.class)
public class DirectJoinServerScreenMixin extends Screen {

    protected DirectJoinServerScreenMixin(final Screen lastScreen, final BooleanConsumer callback, final ServerData serverData) {
        super(Component.translatable("selectServer.direct"));
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        this.addRenderableWidget(
            new Button.Builder(Component.literal("Use last"), button -> ((DirectJoinServerScreenAccessor) this).getIpEdit().setValue(this.minecraft.options.lastMpIp))
                .pos(this.width / 2 + 104, 116)
                .size(50, 20)
                .build()
        );
    }
}
