/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.ICloudServerInfo;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(AddServerScreen.class)
public class AddServerScreenMixin extends Screen {

    @Shadow
    private TextFieldWidget serverNameField;

    @Shadow
    @Final
    private ServerInfo server;

    @Unique
    private Boolean syncWithServer = false;

    protected AddServerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        this.addDrawableChild(
            new ButtonWidget.Builder(Text.literal("Use last"), button -> ((AddServerScreenAccessor) this).getAddressField().setText(this.client != null ? this.client.options.lastServer : ""))
                .position(this.width / 2 + 104, 106)
                .size(50, 20)
                .build()
        );

        this.syncWithServer = this.server != null && ((ICloudServerInfo) this.server).getCloudId() != null;

        this.addDrawableChild(
            CheckboxWidget.builder(Text.literal("Sync with server"), this.textRenderer)
                .pos(this.width / 2 + 104, 66)
                .callback((checkboxWidget, isChecked) -> this.syncWithServer = isChecked)
                .checked(this.syncWithServer)
                .build()
        );
    }

    @Inject(
        method = "init",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/multiplayer/AddServerScreen;serverNameField:Lnet/minecraft/client/gui/widget/TextFieldWidget;",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER)
    )
    private void onInitIncreaseServerNameMaxLength(CallbackInfo info) {
        this.serverNameField.setMaxLength(1000);
    }

    @Inject(method = "addAndClose", at = @At("HEAD"))
    private void onAddAndClose(CallbackInfo info) {
        if (this.syncWithServer) {
            ((ICloudServerInfo) this.server).setCloudId(UUID.randomUUID());
        } else {
            ((ICloudServerInfo) this.server).setCloudId(null);
        }
    }
}
