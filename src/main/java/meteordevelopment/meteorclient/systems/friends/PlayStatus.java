/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.systems.friends;

import com.mojang.util.UUIDTypeAdapter;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.PlayStatusEntry;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.nbt.NbtCompound;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayStatus extends System<PlayStatus> {

    public boolean enabled = false;
    public String server = "";
    public String secret = "";

    private UUID initialId;
    private int counter;
    private int delay;

    public PlayStatus() {
        super("play-status");
    }

    @Override
    public void init() {
        this.initialId = getInitialId();
        this.counter = 0;
        this.delay = 10;
    }

    public static PlayStatus get() {
        return Systems.get(PlayStatus.class);
    }

    private UUID getInitialId() {
        return UUIDTypeAdapter.fromString(mc.getSession().getUuid());
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putBoolean("enabled", this.enabled);
        tag.putString("server", this.server);
        tag.putString("secret", this.secret);
        return tag;
    }

    @Override
    public PlayStatus fromTag(NbtCompound tag) {
        this.enabled = tag.getBoolean("enabled");
        this.server = tag.getString("server");
        this.secret = tag.getString("secret");
        return this;
    }

    @EventHandler
    private static void onGameJoined(GameJoinedEvent event) {
        // set status
    }

    @EventHandler
    private static void onGameLeft(GameLeftEvent event) {
        // remove status
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!this.enabled) return;

        var secondsPassed = 1.0 / TickRate.INSTANCE.getTickRate();
        this.counter += secondsPassed;

        if (this.counter >= this.delay) {

            // set status

            this.counter = 0;
        }
    }

    public PlayStatusEntry[] getPlayStatusEntries() {
        return Http.get(server + "/friendServers").sendJson(PlayStatusEntry[].class);
    }

    private void setPlayStatus() {

    }

    private void removePlayStatus() {

    }
}
