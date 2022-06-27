package Engine.Renderer;

import Platforms.OpenGL.OpenGLTexture;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static Engine.Utils.YH_Log.YH_LOG_ERROR;

public abstract class Texture {
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

    public static @NotNull Texture create(String path) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLTexture(path);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void setData(ByteBuffer data);

    public abstract void bind();

    public abstract void unbind();

    public abstract void delete();
}
