/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.render.ByteTexture;
import meteordevelopment.meteorclient.utils.render.PlayerHeadUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(value = EnvType.CLIENT)
public class PlayStatusServerEntry extends MultiplayerServerListWidget.Entry {
    private final String title;
    private static final String HIDDEN_ADDRESS_TEXT = Text.translatable("selectServer.hiddenAddress").getString();
    private final MultiplayerScreen screen;
    protected final MinecraftClient client;
    protected final LanServerInfo server;
    private long time;

    public PlayStatusServerEntry(MultiplayerScreen screen, LanServerInfo server, String title) {
        this.screen = screen;
        this.server = server;
        this.title = title;
        this.client = MinecraftClient.getInstance();

        var defaultTexture = new ByteTexture(8, 8, PlayerHeadUtils.loadSteveHeadData(), ByteTexture.Format.RGB, ByteTexture.Filter.Nearest, ByteTexture.Filter.Nearest);
        mc.getTextureManager().registerTexture(MeteorClient.identifier(this.server.getMotd().toLowerCase()), defaultTexture);

        new Thread(() -> {
            var skinUrl = PlayerHeadUtils.getSkinUrl(this.server.getMotd());
            if (skinUrl != null) {
                var headTexture = new ByteTexture(8, 8, PlayerHeadUtils.loadHeadData(skinUrl), ByteTexture.Format.RGB, ByteTexture.Filter.Nearest, ByteTexture.Filter.Nearest);
                mc.getTextureManager().registerTexture(MeteorClient.identifier(this.server.getMotd().toLowerCase()), headTexture);
            }
        }).start();
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.drawText(this.client.textRenderer, this.title, x + 32 + 3, y + 1, 0xFFFFFF, false);
        context.drawText(this.client.textRenderer, "Playing as " + this.server.getMotd(), x + 32 + 3, y + 12, 0x808080, false);
        if (this.client.options.hideServerAddress) {
            context.drawText(this.client.textRenderer, HIDDEN_ADDRESS_TEXT, x + 32 + 3, y + 12 + 11, 0x303030, false);
        } else {
            context.drawText(this.client.textRenderer, this.server.getAddressPort(), x + 32 + 3, y + 12 + 11, 0x303030, false);
        }

        this.draw(context, x, y, MeteorClient.identifier(this.server.getMotd().toLowerCase()));
    }

    protected void draw(DrawContext context, int x, int y, Identifier textureId) {
        context.drawTexture(RenderLayer::getGuiTextured, textureId, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.screen.select(this);
        if (Util.getMeasuringTimeMs() - this.time < 250L) {
            this.screen.connect();
            mc.options.lastServer = this.server.getAddressPort();
        }
        this.time = Util.getMeasuringTimeMs();
        return false;
    }

    public LanServerInfo getLanServerEntry() {
        return this.server;
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", Text.literal("").append(title).append(" ").append(this.server.getMotd()));
    }
}
