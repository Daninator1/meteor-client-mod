/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.hud.elements;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import meteordevelopment.meteorclient.systems.hud.*;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.PlayStatusEntry;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

import static meteordevelopment.meteorclient.MeteorClient.mc;

// TODO: fix code duplication
public class PlayStatusHud extends HudElement {
    public static final HudElementInfo<PlayStatusHud> INFO = new HudElementInfo<>(Hud.GROUP, "play-status", "Lets you show the play status of your friends on this server.", PlayStatusHud::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgScale = settings.createGroup("Scale");
    private final SettingGroup sgBackground = settings.createGroup("Background");

    // General

    private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
        .name("shadow")
        .description("Renders shadow behind text.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> primaryColor = sgGeneral.add(new ColorSetting.Builder()
        .name("primary-color")
        .description("Primary color.")
        .defaultValue(new SettingColor())
        .build()
    );

    private final Setting<SettingColor> secondaryColor = sgGeneral.add(new ColorSetting.Builder()
        .name("secondary-color")
        .description("Secondary color.")
        .defaultValue(new SettingColor(175, 175, 175))
        .build()
    );

    private final Setting<Alignment> alignment = sgGeneral.add(new EnumSetting.Builder<Alignment>()
        .name("alignment")
        .description("Horizontal alignment.")
        .defaultValue(Alignment.Auto)
        .build()
    );

    private final Setting<Integer> border = sgGeneral.add(new IntSetting.Builder()
        .name("border")
        .description("How much space to add around the element.")
        .defaultValue(0)
        .build()
    );

    // Scale

    private final Setting<Boolean> customScale = sgScale.add(new BoolSetting.Builder()
        .name("custom-scale")
        .description("Applies custom text scale rather than the global one.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> scale = sgScale.add(new DoubleSetting.Builder()
        .name("scale")
        .description("Custom scale.")
        .visible(customScale::get)
        .defaultValue(1)
        .min(0.5)
        .sliderRange(0.5, 3)
        .build()
    );

    // Background

    private final Setting<Boolean> background = sgBackground.add(new BoolSetting.Builder()
        .name("background")
        .description("Displays background.")
        .defaultValue(false)
        .build()
    );

    private final Setting<SettingColor> backgroundColor = sgBackground.add(new ColorSetting.Builder()
        .name("background-color")
        .description("Color used for the background.")
        .visible(background::get)
        .defaultValue(new SettingColor(25, 25, 25, 50))
        .build()
    );

    public PlayStatusHud() {
        super(INFO);
    }

    @Override
    public void setSize(double width, double height) {
        super.setSize(width + border.get() * 2, height + border.get() * 2);
    }

    @Override
    protected double alignX(double width, Alignment alignment) {
        return box.alignX(getWidth() - border.get() * 2, width, alignment);
    }

    @Override
    public void tick(HudRenderer renderer) {
        double width = renderer.textWidth("Play Status:", shadow.get(), getScale());
        double height = renderer.textHeight(shadow.get(), getScale());

        if (mc.world == null) {
            setSize(width, height);
            return;
        }

        for (PlayStatusEntry playStatusEntry : PlayStatus.get().playStatusEntries) {
            if (playStatusEntry.playerName.equals(mc.player != null ? mc.player.getName().getString() : null)) continue;
            if (!playStatusEntry.server.equals(Utils.getWorldName())) continue;

            String playerText = String.format("%s (%s)", playStatusEntry.playerName, playStatusEntry.name);
            String positionText = String.format("%.0f, %.0f, %.0f (%s)",
                playStatusEntry.position.x,
                playStatusEntry.position.y,
                playStatusEntry.position.z,
                playStatusEntry.dimension);

            width = Math.max(width, renderer.textWidth(String.format("%s %s", playerText, positionText), shadow.get(), getScale()));
            height += renderer.textHeight(shadow.get(), getScale()) + 2;
        }

        setSize(width, height);
    }

    @Override
    public void render(HudRenderer renderer) {
        double y = this.y + border.get();

        if (background.get()) {
            renderer.quad(this.x, this.y, getWidth(), getHeight(), backgroundColor.get());
        }

        renderer.text("Play Status:", x + border.get() + alignX(renderer.textWidth("Players:", shadow.get(), getScale()), alignment.get()), y, secondaryColor.get(), shadow.get(), getScale());

        if (mc.world == null) return;
        if (!PlayStatus.get().enabled) return;
        double spaceWidth = renderer.textWidth(" ", shadow.get(), getScale());

        for (PlayStatusEntry playStatusEntry : PlayStatus.get().playStatusEntries) {
            if (playStatusEntry.playerName.equals(mc.player != null ? mc.player.getName().getString() : null)) continue;
            if (!playStatusEntry.server.equals(Utils.getWorldName())) continue;

            String playerText = String.format("%s (%s)", playStatusEntry.playerName, playStatusEntry.name);
            Color color = primaryColor.get();

            double width = renderer.textWidth(playerText, shadow.get(), getScale());
            width += spaceWidth;

            String positionText = String.format("%.0f, %.0f, %.0f (%s)",
                playStatusEntry.position.x,
                playStatusEntry.position.y,
                playStatusEntry.position.z,
                playStatusEntry.dimension);
            width += renderer.textWidth(positionText, shadow.get(), getScale());

            double x = this.x + border.get() + alignX(width, alignment.get());
            y += renderer.textHeight(shadow.get(), getScale()) + 2;

            x = renderer.text(playerText, x, y, color, shadow.get());
            renderer.text(positionText, x + spaceWidth, y, secondaryColor.get(), shadow.get(), getScale());
        }
    }

    private double getScale() {
        return customScale.get() ? scale.get() : -1;
    }
}
