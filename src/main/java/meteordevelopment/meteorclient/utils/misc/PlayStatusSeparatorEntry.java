/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Objects;

@Environment(value = EnvType.CLIENT)
public class PlayStatusSeparatorEntry extends MultiplayerServerListWidget.ScanningEntry {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public PlayStatusSeparatorEntry() {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        Objects.requireNonNull(this.client.textRenderer);
        var lineWidth = getContentWidth() - 10;
        var posX = (this.client.currentScreen.width / 2 - lineWidth / 2);
        var posY = getContentY() + getContentHeight() / 2;

        context.fill(posX, posY, posX + lineWidth, posY + 1, -1601138544);
    }

    public Text getNarration() {
        return ScreenTexts.EMPTY;
    }

    // isOfSameType still uses the implementation of ScanningEntry - not sure if this is relevant
}
