package Engine.Renderer;


import Engine.Renderer.Buffer.IndexBuffer;
import Engine.Renderer.Buffer.VertexBuffer;
import Platforms.OpenGL.OpenGLVertexArray;

import java.util.List;

import static Engine.Utils.YH_Log.YH_ASSERT;

public abstract class VertexArray {
    public static VertexArray create() {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLVertexArray();
            }
        }

        YH_ASSERT(false, "unknown api type");
        return null;
    }

    public abstract void bind();

    public abstract void unbind();

    public abstract void addVertexBuffer(VertexBuffer vertexBuffer);

    public abstract List<VertexBuffer> getVertexBuffers();

    public abstract IndexBuffer getIndexBuffer();

    public abstract void setIndexBuffer(IndexBuffer indexBuffer);
}
