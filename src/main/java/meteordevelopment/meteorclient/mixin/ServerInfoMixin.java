/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.ICloudServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ServerInfo.class)
public class ServerInfoMixin implements ICloudServerInfo {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Unique
    private UUID cloudId;

    @Inject(method = "toNbt", at = @At("TAIL"))
    private void onToNbt(CallbackInfoReturnable<NbtCompound> cir) {
        var nbtCompound = cir.getReturnValue();

        if (this.cloudId != null) {
            nbtCompound.putString("cloudId", this.cloudId.toString());

        }
    }

    @Inject(method = "fromNbt", at = @At("TAIL"))
    private static void onFromNbt(NbtCompound root, CallbackInfoReturnable<ServerInfo> cir) {
        var serverInfo = cir.getReturnValue();

        if (root.contains("cloudId", 8)) {
            try {
                ((ICloudServerInfo) serverInfo).setCloudId(UUID.fromString(root.getString("cloudId")));
            } catch (IllegalArgumentException illegalArgumentException) {
                LOGGER.warn("Malformed cloud id", illegalArgumentException);
            }
        }
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void onCopyFrom(ServerInfo serverInfo, CallbackInfo ci) {
        this.cloudId = ((ICloudServerInfo) serverInfo).getCloudId();
    }

    @Override
    public UUID getCloudId() {
        return cloudId;
    }

    @Override
    public void setCloudId(UUID cloudId) {
        this.cloudId = cloudId;
    }
}


