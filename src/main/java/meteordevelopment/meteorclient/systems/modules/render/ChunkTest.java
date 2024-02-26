/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;

public class ChunkTest extends Module {

    private Color lineColor = new Color(255, 0, 0, 25);

    private Color sideColor = new Color(0, 255, 0, 25);

    public final HashMap<ChunkPos, Long> latencyMap = new HashMap<>();

    private final int fadeDistance = 12;

    public ChunkTest() {
        super(Categories.Render, "chunk-test", "sers");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (this.isActive()) {
            for (Chunk chunk : Utils.chunks(true)) {
                lineColor = new Color(0, 255, 0, 25);
                sideColor = new Color(0, 255, 0, 25);
//                double dist = PlayerUtils.squaredDistanceTo(chunk.getPos().getCenterX(), mc.player.getY(), chunk.getPos().getCenterZ());
//                double sers = 500000 / dist;
//                double a = scaleValue(sers, 0, dist, 0, 25);
//
//                lineColor.a *= a;
//                sideColor.a *= a;

                var latency = this.latencyMap.get(chunk.getPos());
                if (latency != null) {
                    double badValue = scaleValue(latency, 0, 100, 0, 255);
                    double goodValue = 255 - badValue;
                    lineColor.r = (int) badValue;
                    lineColor.g = (int) goodValue;
                    sideColor.r = (int) badValue;
                    sideColor.g = (int) goodValue;
                }

                renderBox(event, chunk);
            }
        }

        // TODO: latency map should be cleared of all unused chunks
    }

    private static double scaleValue(double value, double originalMin, double originalMax, double newMin, double newMax) {
        // Scale the value
        return ((value - originalMin) * (newMax - newMin) / (originalMax - originalMin)) + newMin;
    }

    private void renderBox(Render3DEvent event, Chunk chunk) {
        double x1 = chunk.getPos().getStartX();
        double y1 = chunk.getBottomY();
        double z1 = chunk.getPos().getStartZ();

        double x2 = chunk.getPos().getEndX() + 1;
        double y2 = 50;
        double z2 = chunk.getPos().getEndZ() + 1;

        event.renderer.box(x1, y1, z1, x2, y2, z2, sideColor, lineColor, ShapeMode.Both, 0);
    }
}
