package Engine.Renderer.Buffer;

import Engine.Renderer.RendererCommandAPI;
import Platforms.OpenGL.OpenGLVertexBuffer;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static Engine.Utils.YH_Log.YH_LOG_ERROR;

public abstract class VertexBuffer {
    protected BufferLayout bufferLayout;

    public static VertexBuffer create(float[] vertices) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLVertexBuffer(vertices);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

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