/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.render.postprocess;

import meteordevelopment.meteorclient.renderer.ShapeMode;
import net.minecraft.entity.Entity;

public class ChunkOutlineShader extends PostProcessShader {

    public ChunkOutlineShader() {
        init("outline");
    }

    @Override
    protected void preDraw() {
        framebuffer.clear(false);
        framebuffer.beginWrite(false);
    }

    @Override
    protected boolean shouldDraw() {
        return true;
    }

    @Override
    public boolean shouldDraw(Entity entity) {
        return true;
    }

    @Override
    protected void setUniforms() {
        shader.set("u_Width", 1);
        shader.set("u_FillOpacity", 50 / 255.0);
        shader.set("u_ShapeMode", ShapeMode.Both.ordinal());
        shader.set("u_GlowMultiplier", 3.5);
    }
}
