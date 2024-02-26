/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.ChunkTest;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

import static meteordevelopment.meteorclient.MeteorClient.LOG;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {

    @Unique
    private long lastChunkCenterTime = 0;

    @Inject(method = "setChunkMapCenter", at = @At("HEAD"))
    public void onSetChunkMapCenter(int x, int z, CallbackInfo ci) {
        if (Modules.get().get(ChunkTest.class).isActive()) {
            LOG.info("Setting chunk center to x: " + x + " z: " + z);
            lastChunkCenterTime = System.currentTimeMillis();
        }
    }

    @Inject(method = "loadChunkFromPacket", at = @At("HEAD"))
    private void onLoadChunkFromPacket(int x, int z, PacketByteBuf buf, NbtCompound nbt, Consumer<ChunkData.BlockEntityVisitor> consumer, CallbackInfoReturnable<WorldChunk> cir) {
        if (Modules.get().get(ChunkTest.class).isActive()) {
            long delay = System.currentTimeMillis() - lastChunkCenterTime;
            LOG.info("Loading chunk at x: " + x + " z: " + z + " after " + delay + "ms");
            var chunkPos = new ChunkPos(x, z);
            Modules.get().get(ChunkTest.class).latencyMap.put(chunkPos, delay);
        }
    }
}
