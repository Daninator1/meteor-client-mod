/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value = EnvType.CLIENT)
public class FriendServerEntry extends MultiplayerServerListWidget.Entry {
    private static final int field_32386 = 32;
    private static final Text TITLE_TEXT = new TranslatableText("FRIEND_NAME");
    private static final Text HIDDEN_ADDRESS_TEXT = new TranslatableText("selectServer.hiddenAddress");
    private final MultiplayerScreen screen;
    protected final MinecraftClient client;
    protected final LanServerInfo server;
    private long time;

    public FriendServerEntry(MultiplayerScreen screen, LanServerInfo server) {
        this.screen = screen;
        this.server = server;
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.client.textRenderer.draw(matrices, TITLE_TEXT, (float) (x + 32 + 3), (float) (y + 1), 0xFFFFFF);
        this.client.textRenderer.draw(matrices, this.server.getMotd(), (float) (x + 32 + 3), (float) (y + 12), 0x808080);
        if (this.client.options.hideServerAddress) {
            this.client.textRenderer.draw(matrices, HIDDEN_ADDRESS_TEXT, (float) (x + 32 + 3), (float) (y + 12 + 11), 0x303030);
        } else {
            this.client.textRenderer.draw(matrices, this.server.getAddressPort(), (float) (x + 32 + 3), (float) (y + 12 + 11), 0x303030);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.screen.select(this);
        if (Util.getMeasuringTimeMs() - this.time < 250L) {
            this.screen.connect();
        }
        this.time = Util.getMeasuringTimeMs();
        return false;
    }

    public LanServerInfo getLanServerEntry() {
        return this.server;
    }

    @Override
    public Text getNarration() {
        return new TranslatableText("narrator.select", new LiteralText("").append(TITLE_TEXT).append(" ").append(this.server.getMotd()));
    }
}
