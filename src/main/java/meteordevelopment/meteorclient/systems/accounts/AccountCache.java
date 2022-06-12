/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.accounts;

import com.mojang.util.UUIDTypeAdapter;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.NbtException;
import meteordevelopment.meteorclient.utils.render.PlayerHeadTexture;
import meteordevelopment.meteorclient.utils.render.PlayerHeadUtils;
import meteordevelopment.meteorclient.renderer.Texture;
import net.minecraft.nbt.NbtCompound;

public class AccountCache implements ISerializable<AccountCache> {
    public String username = "";
    public String uuid = "";
    private PlayerHeadTexture headTexture;

    public PlayerHeadTexture getHeadTexture() {
        return headTexture != null ? headTexture : PlayerHeadUtils.STEVE_HEAD;
    }

    public void loadHead() {
        if (uuid == null || uuid.isBlank()) return;
        headTexture = PlayerHeadUtils.fetchHead(UUIDTypeAdapter.fromString(uuid));
    }
    
    public boolean loadHead(String url) {
        var head = AccountUtils.loadHeadData(url);
        if (head.length == 0) return false;
        headTexture = new Texture(8, 8, head, Texture.Format.RGB, Texture.Filter.Nearest, Texture.Filter.Nearest);
        return true;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putString("username", username);
        tag.putString("uuid", uuid);

        return tag;
    }

    @Override
    public AccountCache fromTag(NbtCompound tag) {
        if (!tag.contains("username") || !tag.contains("uuid")) throw new NbtException();

        username = tag.getString("username");
        uuid = tag.getString("uuid");
        loadHead();

        return this;
    }
}
