/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Objects;

@Environment(value = EnvType.CLIENT)
public class PlayStatusSeparatorEntry extends MultiplayerServerListWidget.Entry {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public PlayStatusSeparatorEntry() {
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        Objects.requireNonNull(this.client.textRenderer);
        var lineWidth = entryWidth - 10;
        var posX = (this.client.currentScreen.width / 2 - lineWidth / 2);
        var posY = y + entryHeight / 2;

        DrawableHelper.fill(matrices, posX, posY, posX + lineWidth, posY + 1, -1601138544);
    }

    public Text getNarration() {
        return ScreenTexts.EMPTY;
    }
}
