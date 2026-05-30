package meteordevelopment.meteorclient.utils.render;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.network.Http;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerHeadTexture extends Texture {

    public PlayerHeadTexture(byte[] head) {
        super(8, 8, TextureFormat.RGBA8, FilterMode.NEAREST, FilterMode.NEAREST);

        upload(BufferUtils.createByteBuffer(head.length).put(head));
    }

    public PlayerHeadTexture() {
        super(8, 8, TextureFormat.RGBA8, FilterMode.NEAREST, FilterMode.NEAREST);

        try (InputStream inputStream = mc.getResourceManager().getResource(MeteorClient.identifier("textures/steve.png")).get().open()) {
            ByteBuffer data = TextureUtil.readResource(inputStream);
            data.rewind();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);

                ByteBuffer image = STBImage.stbi_load_from_memory(data, width, height, comp, 4);
                upload(image);
                STBImage.stbi_image_free(image);
            }
            MemoryUtil.memFree(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] downloadHead(String url, boolean needsRotate) throws IOException {
        BufferedImage skin;
        try (InputStream in = Http.get(url).sendInputStream()) {
            skin = ImageIO.read(in);
        }

        if (skin == null) throw new IOException("Failed to decode skin image.");

        byte[] head = new byte[8 * 8 * 4];
        int[] pixel = new int[4];

        int i = 0;
        for (int x = 8; x < 16; x++) {
            for (int y = 8; y < 16; y++) {
                skin.getData().getPixel(x, y, pixel);

                for (int j = 0; j < 4; j++) {
                    head[i++] = (byte) pixel[j];
                }
            }
        }

        i = 0;
        for (int x = 40; x < 48; x++) {
            for (int y = 8; y < 16; y++) {
                skin.getData().getPixel(x, y, pixel);

                if (pixel[3] != 0) {
                    for (int j = 0; j < 4; j++) {
                        head[i++] = (byte) pixel[j];
                    }
                } else i += 4;
            }
        }

        if (needsRotate) head = rotateHeadClockwise(head);

        return head;
    }

    private static byte[] rotateHeadClockwise(byte[] head) {
        byte[] rotated = new byte[head.length];

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int sourceIndex = (y * 8 + x) * 4;
                int destinationIndex = (x * 8 + (7 - y)) * 4;

                System.arraycopy(head, sourceIndex, rotated, destinationIndex, 4);
            }
        }

        return rotated;
    }
}
