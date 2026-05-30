/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.entity.BoatMoveEvent;
import meteordevelopment.meteorclient.events.entity.EntityMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityFly extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("Horizontal speed in blocks per second.")
        .defaultValue(10)
        .min(0)
        .sliderMax(50)
        .build()
    );

    private final Setting<Double> verticalSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("vertical-speed")
        .description("Vertical speed in blocks per second.")
        .defaultValue(6)
        .min(0)
        .sliderMax(20)
        .build()
    );

    private final Setting<Double> fallSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("fall-speed")
        .description("How fast you fall in blocks per second.")
        .defaultValue(0.1)
        .min(0)
        .build()
    );

    private final Setting<Boolean> cancelServerPackets = sgGeneral.add(new BoolSetting.Builder()
        .name("cancel-server-packets")
        .description("Cancels incoming boat move packets.")
        .defaultValue(false)
        .build()
    );

    public EntityFly() {
        super(Categories.Movement, "entity-fly", "Allows you to fly on any rideable entity.");
    }

    @EventHandler
    private void onBoatMove(BoatMoveEvent event) {
        Fly(event.boat, event.boat.getKnownSpeed());
    }

    @EventHandler
    private void onEntityMove(EntityMoveEvent event) {
        Fly(event.entity, event.movement);
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof ServerboundMoveVehiclePacket && cancelServerPackets.get()) {
            event.cancel();
        }
    }

    private void Fly(Entity entity, Vec3 velocity) {
        if (entity.getControllingPassenger() != mc.player) return;

        if (mc.player != null) {
            entity.setYRot(mc.player.yHeadRot);
        }

        // Horizontal movement
        Vec3 vel = PlayerUtils.getHorizontalVelocity(speed.get());
        double velX = vel.x();
        double velY = 0;
        double velZ = vel.z();

        // Vertical movement
        if (mc.options.keyJump.isDown()) velY += verticalSpeed.get() / 20;
        if (mc.options.keySprint.isDown()) velY -= verticalSpeed.get() / 20;
        else velY -= fallSpeed.get() / 20;

        // Apply velocity
        ((IVec3) velocity).meteor$set(velX, velY, velZ);
    }
}
