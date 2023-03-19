/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.utils.render.ByteTexture;
import meteordevelopment.meteorclient.utils.render.PlayerHeadUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(value = EnvType.CLIENT)
public class PlayStatusServerEntry extends MultiplayerServerListWidget.Entry {
    private final Text titleText;
    private static final Text HIDDEN_ADDRESS_TEXT = Text.translatable("selectServer.hiddenAddress");
    private final MultiplayerScreen screen;
    protected final MinecraftClient client;
    protected final LanServerInfo server;
    private long time;

    public PlayStatusServerEntry(MultiplayerScreen screen, LanServerInfo server, String title) {
        this.screen = screen;
        this.server = server;
        titleText = Text.of(title);
        this.client = MinecraftClient.getInstance();

        var defaultTexture = new ByteTexture(8, 8, PlayerHeadUtils.loadSteveHeadData(), ByteTexture.Format.RGB, ByteTexture.Filter.Nearest, ByteTexture.Filter.Nearest);
        mc.getTextureManager().registerTexture(new Identifier(this.server.getMotd().toLowerCase()), defaultTexture);

        new Thread(() -> {
            var skinUrl = PlayerHeadUtils.getSkinUrl(this.server.getMotd());
            if (skinUrl != null) {
                var headTexture = new ByteTexture(8, 8, PlayerHeadUtils.loadHeadData(skinUrl), ByteTexture.Format.RGB, ByteTexture.Filter.Nearest, ByteTexture.Filter.Nearest);
                mc.getTextureManager().registerTexture(new Identifier(this.server.getMotd().toLowerCase()), headTexture);
            }
        }).start();
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.client.textRenderer.draw(matrices, titleText, (float) (x + 32 + 3), (float) (y + 1), 0xFFFFFF);
        this.client.textRenderer.draw(matrices, "Playing as " + this.server.getMotd(), (float) (x + 32 + 3), (float) (y + 12), 0x808080);
        if (this.client.options.hideServerAddress) {
            this.client.textRenderer.draw(matrices, HIDDEN_ADDRESS_TEXT, (float) (x + 32 + 3), (float) (y + 12 + 11), 0x303030);
        } else {
            this.client.textRenderer.draw(matrices, this.server.getAddressPort(), (float) (x + 32 + 3), (float) (y + 12 + 11), 0x303030);
        }

        this.draw(matrices, x, y, new Identifier(this.server.getMotd().toLowerCase()));
    }

    protected void draw(MatrixStack matrices, int x, int y, Identifier textureId) {
        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.enableBlend();
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
        RenderSystem.disableBlend();
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
        return Text.translatable("narrator.select", Text.literal("").append(titleText).append(" ").append(this.server.getMotd()));
    }
}
