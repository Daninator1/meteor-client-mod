/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.entity.BoatMoveEvent;
import meteordevelopment.meteorclient.events.entity.LivingEntityMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.util.math.Vec3d;

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
        Fly(event.boat, event.boat.getVelocity());
    }

    @EventHandler
    private void onLivingEntityMove(LivingEntityMoveEvent event) {
        Fly(event.entity, event.movement);
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof VehicleMoveS2CPacket && cancelServerPackets.get()) {
            event.cancel();
        }
    }

    private void Fly(Entity entity, Vec3d velocity) {
        if (entity.getControllingPassenger() != mc.player) return;

        entity.setYaw(mc.player.getYaw());

        // Horizontal movement
        Vec3d vel = PlayerUtils.getHorizontalVelocity(speed.get());
        double velX = vel.getX();
        double velY = 0;
        double velZ = vel.getZ();

        // Vertical movement
        if (mc.options.jumpKey.isPressed()) velY += verticalSpeed.get() / 20;
        if (mc.options.sprintKey.isPressed()) velY -= verticalSpeed.get() / 20;
        else velY -= fallSpeed.get() / 20;

        // Apply velocity
        ((IVec3d) velocity).meteor$set(velX, velY, velZ);
    }
}
