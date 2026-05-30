/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import meteordevelopment.meteorclient.mixininterface.ISyncedServerData;
import meteordevelopment.meteorclient.systems.friends.ServerSync;
import meteordevelopment.meteorclient.utils.misc.SyncedServerInfo;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ManageServerScreen.class)
public class ManageServerScreenMixin extends Screen {

    @Shadow
    private EditBox nameEdit;

    @Shadow
    @Final
    private ServerData serverData;

    @Unique
    private Boolean syncWithServer = false;

    protected ManageServerScreenMixin(final Screen lastScreen, final Component title, final BooleanConsumer callback, final ServerData serverData) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        this.addRenderableWidget(
            new Button.Builder(Component.literal("Use last"), button -> ((ManageServerScreenAccessor) this).getIpEdit().setValue(this.minecraft.options.lastMpIp))
                .pos(this.width / 2 + 104, 106)
                .size(50, 20)
                .build()
        );

        if (ServerSync.get().enabled) {
            this.syncWithServer = this.serverData != null && ((ISyncedServerData) this.serverData).meteor$getId() != null;



            // if this is already true, it means that the server is already synced which cannot be changed except by removing the server
            if (this.syncWithServer) {
                var syncedText = "✅ Synced";
                this.addRenderableWidget(
                    new StringWidget(
                        this.width / 2 + 104,
                        71,
                        this.minecraft.font.width(syncedText),
                        this.minecraft.font.lineHeight,
                        Component.literal(syncedText),
                        this.minecraft.font)
                );
            } else {
                this.addRenderableWidget(
                    Checkbox.builder(Component.literal("Sync"), this.minecraft.font)
                        .pos(this.width / 2 + 104, 68)
                        .onValueChange((checkboxWidget, isChecked) -> this.syncWithServer = isChecked)
                        .selected(this.syncWithServer)
                        .build()
                );
            }
        }
    }

    @Inject(
        method = "init",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screens/ManageServerScreen;nameEdit:Lnet/minecraft/client/gui/components/EditBox;",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER)
    )
    private void onInitIncreaseServerNameMaxLength(CallbackInfo info) {
        this.nameEdit.setMaxLength(1000);
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void onAddAndClose(CallbackInfo info) {
        if (!ServerSync.get().enabled) return;

        if (this.syncWithServer) {
            if (((ISyncedServerData) this.serverData).meteor$getId() == null) {
                ((ISyncedServerData) this.serverData).meteor$setId(UUID.randomUUID());
            }

            MeteorExecutor.execute(() -> ServerSync.get().addOrUpdateServer(new SyncedServerInfo(((ISyncedServerData) this.serverData).meteor$getId(), this.serverData.name, this.serverData.ip)));
        } else {
            ((ISyncedServerData) this.serverData).meteor$setId(null);
        }
    }
}
