/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;

@Environment(value = EnvType.CLIENT)
public class PlayStatusSeparatorEntry extends ServerSelectionList.LANHeader {

    @Override
    public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
        var posX = getContentX();
        var posY = getContentY() + getContentHeight() / 2;

        graphics.fill(posX, posY, posX + getContentWidth(), posY + 1, -1601138544);
    }

    // isOfSameType still uses the implementation of ScanningEntry - not sure if this is relevant
}
