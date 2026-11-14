package GameEngine.Engine.Renderer;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;

import GameEngine.Engine.Renderer.Buffer.IndexBuffer;
import GameEngine.Engine.Renderer.Buffer.VertexBuffer;
import GameEngine.Platforms.OpenGL.OpenGLVertexArray;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public abstract class VertexArray {
    public static @NotNull VertexArray create() {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLVertexArray();
            }
        }

        YH_ASSERT(false, "unknown api type");
        return null;
    }

    public abstract void delete();

    public abstract void bind();

    public abstract void unbind();

    public abstract void addVertexBuffer(VertexBuffer vertexBuffer);

    public abstract List<VertexBuffer> getVertexBuffers();

    public abstract IndexBuffer getIndexBuffer();

    public abstract void setIndexBuffer(IndexBuffer indexBuffer);
}
