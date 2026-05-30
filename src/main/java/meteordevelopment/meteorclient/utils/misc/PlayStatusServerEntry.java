/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.PlayerHeadTexture;
import meteordevelopment.meteorclient.utils.render.PlayerHeadUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.Component;

import java.io.IOException;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(value = EnvType.CLIENT)
public class PlayStatusServerEntry extends ServerSelectionList.NetworkServerEntry {
    private static final String HIDDEN_ADDRESS_TEXT = Component.translatable("selectServer.hiddenAddress").getString();
    private final Component title;
    private final JoinMultiplayerScreen screen;

    public PlayStatusServerEntry(final JoinMultiplayerScreen screen, final LanServer serverData, String title) {
        super(screen, serverData);
        this.title = Component.literal(title);
        this.screen = screen;

        mc.execute(() -> {
            // could not use the STEVE_HEAD of PlayerHeadUtils as after a server list refresh it loses its texture
            mc.getTextureManager().register(MeteorClient.identifier(this.serverData.getMotd().toLowerCase()), new PlayerHeadTexture());
        });

        MeteorExecutor.execute(() -> {
            var skinUrl = PlayerHeadUtils.getSkinUrl(this.serverData.getMotd());
            if (skinUrl != null) {
                mc.execute(() -> {
                    PlayerHeadTexture headTexture = new PlayerHeadTexture();
                    try {
                        headTexture = new PlayerHeadTexture(PlayerHeadTexture.downloadHead(skinUrl, true));
                    } catch (IOException _) {
                    }
                    mc.getTextureManager().register(MeteorClient.identifier(this.serverData.getMotd().toLowerCase()), headTexture);
                });
            }
        });
    }

    @Override
    public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
        graphics.text(this.minecraft.font, this.title, getContentX() + 32 + 3, getContentY() + 1, -1);
        graphics.text(this.minecraft.font, Component.literal("Playing as " + this.serverData.getMotd()), getContentX() + 32 + 3, getContentY() + 12, -8355712);
        if (this.minecraft.options.hideServerAddress) {
            graphics.text(this.minecraft.font, HIDDEN_ADDRESS_TEXT, getContentX() + 32 + 3, getContentY() + 12 + 11, -13619152);
        } else {
            graphics.text(this.minecraft.font, this.serverData.getAddress(), getContentX() + 32 + 3, getContentY() + 12 + 11, -13619152);
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, MeteorClient.identifier(this.serverData.getMotd().toLowerCase()), getContentX(), getContentY(), 0, 0, 32, 32, 32, 32);
    }

    @Override
    public void join() {
        this.screen.join(new ServerData(this.serverData.getMotd(), this.serverData.getAddress(), ServerData.Type.OTHER));
        mc.options.lastMpIp = this.serverData.getAddress();
    }

    // isOfSameType still uses the implementation of LanServerEntry - not sure if this is relevant
}
