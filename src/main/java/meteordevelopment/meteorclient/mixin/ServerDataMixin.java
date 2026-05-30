/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.ISyncedServerData;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ServerData.class)
public class ServerDataMixin implements ISyncedServerData {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Unique
    private UUID id;

    @Inject(method = "write", at = @At("TAIL"))
    private void onToNbt(CallbackInfoReturnable<CompoundTag> cir) {
        var nbtCompound = cir.getReturnValue();

        if (this.id != null) {
            nbtCompound.putString("id", this.id.toString());

        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private static void onFromNbt(CompoundTag tag, CallbackInfoReturnable<ServerData> cir) {
        var serverData = cir.getReturnValue();

        tag.getString("id").ifPresent(
            value -> {
                try {
                    ((ISyncedServerData) serverData).meteor$setId(UUID.fromString(value));
                } catch (IllegalArgumentException illegalArgumentException) {
                    LOGGER.warn("Malformed server id", illegalArgumentException);
                }
            }
        );
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void onCopyFrom(ServerData other, CallbackInfo ci) {
        this.id = ((ISyncedServerData) other).meteor$getId();
    }

    @Override
    public UUID meteor$getId() {
        return id;
    }

    @Override
    public void meteor$setId(UUID id) {
        this.id = id;
    }
}


