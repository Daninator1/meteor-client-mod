package meteordevelopment.meteorclient.utils.render;

import com.google.gson.Gson;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.util.UndashedUuid;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.accounts.ProfileResponse;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.accounts.TexturesJson;
import meteordevelopment.meteorclient.systems.accounts.UuidToProfileResponse;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.util.Identifier;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Base64;
import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerHeadUtils {
    public static PlayerHeadTexture STEVE_HEAD;

    private PlayerHeadUtils() {
    }

    @PostInit
    public static void init() {
        STEVE_HEAD = new PlayerHeadTexture();
    }

    public static PlayerHeadTexture fetchHead(UUID id) {
        if (id == null) return null;

        String url = getSkinUrl(id);
        return url != null ? new PlayerHeadTexture(url) : null;
    }

    public static String getSkinUrl(UUID id) {
        return getSkinUrlInternal(id);
    }

    public static String getSkinUrl(String username) {
        ProfileResponse res = Http.get("https://api.mojang.com/users/profiles/minecraft/" + username).sendJson(ProfileResponse.class);
        if (res == null) return null;
        return getSkinUrlInternal(UndashedUuid.fromString(res.id));
    }

    public static String getSkinUrlInternal(UUID id) {
        UuidToProfileResponse res2 = Http.get("https://sessionserver.mojang.com/session/minecraft/profile/" + id)
            .exceptionHandler(e -> MeteorClient.LOG.error("Could not contact mojang session servers.", e))
            .sendJson(UuidToProfileResponse.class);
        if (res2 == null) return null;

        String base64Textures = res2.getPropertyValue("textures");
        if (base64Textures == null) return null;

        TexturesJson textures = new Gson().fromJson(new String(Base64.getDecoder().decode(base64Textures)), TexturesJson.class);
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
