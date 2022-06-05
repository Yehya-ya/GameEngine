package Engine.Renderer;

import org.joml.Vector4f;

public abstract class RendererCommand {
    public abstract void setClearColor(Vector4f color);

    public abstract void clear();

    public abstract void drawIndexed(VertexArray vertexArray);

    public abstract API getApi();

    public enum API {
        None, OpenGL
    }
}