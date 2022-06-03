/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.LecternCrash;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LecternScreen.class)
public abstract class LecternScreenMixin extends BookScreen implements ScreenHandlerProvider<LecternScreenHandler> {

    @Override
    public void init() {
        super.init();

        LecternCrash lecternCrash = Modules.get().get(LecternCrash.class);

        if (lecternCrash.isActive()) {
            addDrawableChild(new ButtonWidget(
                4,
                28,
                120,
                20,
                new LiteralText("Crash"),
                button -> lecternCrash.crash())
            );
        }
    }
}
