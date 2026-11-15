package GameEngine.Engine.Renderer;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

import GameEngine.Engine.Utils.Resources;
import GameEngine.Platforms.OpenGL.OpenGLTexture;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

public abstract class Texture {
    public static final List<String> ImageExtensions = List.of(".png", ".jpg", ".jpeg", ".jpe");
    protected final String path;
    protected int width, height;

    public Texture(String path) {
        this.path = path;
    }

    public static @NotNull Texture create(int width, int height) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLTexture(width, height);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public static @NotNull Texture create(@NotNull Path file) {
        return fromEncodedImage(file.toString(), readFile(file));
    }

    public static @NotNull Texture createFromResource(String resourceName) {
        return fromEncodedImage(resourceName, Resources.readBytes(resourceName));
    }

    public static @NotNull Texture createWhite() {
        Texture texture = create(1, 1);
        byte white = (byte) 0xFF;
        texture.setData(BufferUtils.createByteBuffer(4)
                .put(new byte[] {white, white, white, white})
                .flip());
        return texture;
    }

    public static boolean isImageFile(@NotNull Path file) {
        String fileName = file.toString().toLowerCase(Locale.ROOT);
        boolean hasImageExtension = ImageExtensions.stream().anyMatch(fileName::endsWith);
        return hasImageExtension && Files.isRegularFile(file);
    }

    private static @NotNull Texture fromEncodedImage(String path, byte[] encodedImage) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLTexture(path, encodedImage);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    private static byte[] readFile(Path file) {
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new TextureLoadException("Failed to read the image \"" + file + "\"", e);
        }
    }

    public String getPath() {
        return path;
    }

    public abstract int getRendererId();

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void setData(ByteBuffer data);

    public abstract void bind(int slot);

    public abstract void unbind();

    public abstract void delete();
}
