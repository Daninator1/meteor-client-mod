/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.systems.friends;

import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.misc.SyncedServerInfo;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.nbt.CompoundTag;

import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.LOG;

public class ServerSync extends System<ServerSync> {

    public boolean enabled = false;
    public String server = "";
    public String apiKey = "";
    public SyncedServerInfo[] syncedServerInfos;

    public ServerSync() {
        super("server-sync");
    }

    @Override
    public void init() {
        this.syncedServerInfos = new SyncedServerInfo[0];
    }

    public static ServerSync get() {
        return Systems.get(ServerSync.class);
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("enabled", this.enabled);
        tag.putString("server", this.server);
        tag.putString("apiKey", this.apiKey);
        return tag;
    }

    @Override
    public ServerSync fromTag(CompoundTag tag) {
        this.enabled = tag.getBooleanOr("enabled", this.enabled);
        this.server = tag.getStringOr("server", this.server);
        this.apiKey = tag.getStringOr("apiKey", this.apiKey);
        return this;
    }

    public Optional<SyncedServerInfo[]> getServers() {
        try {
            HttpResponse<SyncedServerInfo[]> httpResponse = Http
                .get(this.server + "/server")
                .apiKey(this.apiKey)
                .exceptionHandler(e -> LOG.error("Failed to fetch synced servers", e))
                .sendJsonResponse(SyncedServerInfo[].class);

            if (httpResponse.statusCode() != Http.SUCCESS) {
                LOG.warn("Failed to fetch synced servers: HTTP status {}", httpResponse.statusCode());
                return Optional.empty();
            }

            SyncedServerInfo[] response = httpResponse.body();
            if (response == null) {
                LOG.warn("Failed to fetch synced servers: response body was empty");
                return Optional.empty();
            }

            for (SyncedServerInfo serverInfo : response) {
                if (serverInfo == null || serverInfo.id == null || serverInfo.name == null || serverInfo.name.isBlank() || serverInfo.address == null || serverInfo.address.isBlank()) {
                    LOG.warn("Failed to fetch synced servers: response contains an incomplete server entry");
                    return Optional.empty();
                }
            }

            return Optional.of(response);
        } catch (Exception e) {
            LOG.error("Failed to fetch synced servers", e);
        }

        return Optional.empty();
    }

    public void addOrUpdateServer(SyncedServerInfo serverInfo) {
        try {
            Http.post(this.server + "/server")
                .apiKey(this.apiKey)
                .bodyJson(serverInfo)
                .send();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public void removeServer(UUID id) {
        try {
            Http.delete(this.server + "/server/" + id)
                .apiKey(this.apiKey)
                .send();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
