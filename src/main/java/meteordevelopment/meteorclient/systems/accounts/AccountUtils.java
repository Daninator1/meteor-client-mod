/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.accounts;

import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.blaze3d.platform.TextureUtil;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.render.EntityOwner;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.util.Identifier;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.include.com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Base64;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class AccountUtils {
    public static void setBaseUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("baseUrl");
            field.setAccessible(true);
            field.set(service, url);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void setJoinUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("joinUrl");
            field.setAccessible(true);
            field.set(service, new URL(url));
        } catch (IllegalAccessException | NoSuchFieldException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void setCheckUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("checkUrl");
            field.setAccessible(true);
            field.set(service, new URL(url));
        } catch (IllegalAccessException | NoSuchFieldException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static String getSkinUrl(String username) {
        ProfileResponse res = Http.get("https://api.mojang.com/users/profiles/minecraft/" + username).sendJson(ProfileResponse.class);
        if (res == null) return null;

        UuidToProfileResponse res2 = Http.get("https://sessionserver.mojang.com/session/minecraft/profile/" + res.getPropertyValue("id")).sendJson(UuidToProfileResponse.class);
        if (res2 == null) return null;

        String base64Textures = res2.getPropertyValue("textures");
        if (base64Textures == null) return null;

        TexturesJson textures = new com.google.gson.Gson().fromJson(new String(Base64.getDecoder().decode(base64Textures)), TexturesJson.class);

        if (textures.textures.SKIN == null) return null;

        return textures.textures.SKIN.url;
    }

    public static byte[] loadHeadData(String url) {
        try {
            BufferedImage skin = ImageIO.read(Http.get(url).sendInputStream());
            byte[] head = new byte[8 * 8 * 3];
            int[] pixel = new int[4];

            int i = 0;
            for (int x = 8; x < 16; x++) {
                for (int y = 8; y < 16; y++) {
                    skin.getData().getPixel(y, x, pixel);

                    for (int j = 0; j < 3; j++) {
                        head[i] = (byte) pixel[j];
                        i++;
                    }
                }
            }

            i = 0;
            for (int x = 8; x < 16; x++) {
                for (int y = 40; y < 48; y++) {
                    skin.getData().getPixel(y, x, pixel);

                    if (pixel[3] != 0) {
                        for (int j = 0; j < 3; j++) {
                            head[i] = (byte) pixel[j];
                            i++;
                        }
                    }
                    else i += 3;
                }
            }

            return head;
        } catch (IOException e) {
            MeteorClient.LOG.error("Failed to read skin url (" + url + ").");
            return new byte[0];
        }
    }

    public static ByteBuffer loadSteveHeadData() {
        try {
            ByteBuffer data = TextureUtil.readResource(mc.getResourceManager().getResource(new Identifier(MeteorClient.MOD_ID, "textures/steve.png")).get().getInputStream());
            data.rewind();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);

                ByteBuffer image = STBImage.stbi_load_from_memory(data, width, height, comp, 3);

                STBImage.stbi_image_free(image);

                return image;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
