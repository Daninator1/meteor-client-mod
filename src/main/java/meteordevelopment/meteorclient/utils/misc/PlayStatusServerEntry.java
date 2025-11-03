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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(value = EnvType.CLIENT)
public class PlayStatusServerEntry extends MultiplayerServerListWidget.LanServerEntry {
    private final String title;
    private static final String HIDDEN_ADDRESS_TEXT = Text.translatable("selectServer.hiddenAddress").getString();
    private final MultiplayerScreen screen;
    protected final MinecraftClient client;
    protected final LanServerInfo server;
    private long time;

    public PlayStatusServerEntry(MultiplayerScreen screen, LanServerInfo server, String title) {
        super(screen, server);
        this.screen = screen;
        this.server = server;
        this.title = title;
        this.client = MinecraftClient.getInstance();

        mc.execute(() -> {
            // could not use the STEVE_HEAD of PlayerHeadUtils as after a server list refresh it loses its texture
            mc.getTextureManager().registerTexture(MeteorClient.identifier(this.server.getMotd().toLowerCase()), new PlayerHeadTexture());
        });

        MeteorExecutor.execute(() -> {
            var skinUrl = PlayerHeadUtils.getSkinUrl(this.server.getMotd());
            if (skinUrl != null) {
                mc.execute(() -> {
                    var headTexture = new PlayerHeadTexture(skinUrl);
                    mc.getTextureManager().registerTexture(MeteorClient.identifier(this.server.getMotd().toLowerCase()), headTexture);
                });
            }
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawTextWithShadow(this.client.textRenderer, this.title, getX() + 32 + 3, getY() + 1, -1);
        context.drawTextWithShadow(this.client.textRenderer, "Playing as " + this.server.getMotd(), getContentX() + 32 + 3, getContentY() + 12, -8355712);
        if (this.client.options.hideServerAddress) {
            context.drawTextWithShadow(this.client.textRenderer, HIDDEN_ADDRESS_TEXT, getContentX() + 32 + 3, getContentY() + 12 + 11, -13619152);
        } else {
            context.drawTextWithShadow(this.client.textRenderer, this.server.getAddressPort(), getContentX() + 32 + 3, getContentY() + 12 + 11, -13619152);
        }

        this.draw(context, getContentX(), getContentY(), MeteorClient.identifier(this.server.getMotd().toLowerCase()));
    }

    protected void draw(DrawContext context, int x, int y, Identifier textureId) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, textureId, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
    }

    @Override
    public void connect() {
        this.screen.connect(new ServerInfo(this.server.getMotd(), this.server.getAddressPort(), ServerInfo.ServerType.OTHER));
        mc.options.lastServer = this.server.getAddressPort();
    }

    public LanServerInfo getLanServerEntry() {
        return this.server;
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", Text.literal("").append(title).append(" ").append(this.server.getMotd()));
    }

    // isOfSameType still uses the implementation of LanServerEntry - not sure if this is relevant
}
