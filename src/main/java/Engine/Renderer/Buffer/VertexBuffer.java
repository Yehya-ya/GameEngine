package Engine.Renderer.Buffer;

import Engine.Renderer.RendererCommandAPI;
import Platforms.OpenGL.OpenGLVertexBuffer;
import org.jetbrains.annotations.NotNull;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static Engine.Utils.YH_Log.YH_LOG_ERROR;

public abstract class VertexBuffer {
    protected BufferLayout bufferLayout;

    public static @NotNull VertexBuffer create(long size) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLVertexBuffer(size);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public static @NotNull VertexBuffer create(float[] vertices) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLVertexBuffer(vertices);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public abstract void setData(float[] data);

    public abstract void delete();

    public abstract void bind();

    public abstract void unbind();

    public BufferLayout getLayout() {
        return bufferLayout;
    }

    public void setLayout(BufferLayout bufferLayout) {
        this.bufferLayout = bufferLayout;
    }
}
