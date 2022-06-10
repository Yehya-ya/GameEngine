package Engine.Renderer;

import Platforms.OpenGL.OpenGLRendererCommand;
import org.joml.Vector4f;

public class RendererCommandAPI {
    private static final RendererCommand rendererCommand = new OpenGLRendererCommand();

    private RendererCommandAPI() {
    }

    public static void init() {
        rendererCommand.init();
    }

    public static void setClearColor(Vector4f color) {
        rendererCommand.setClearColor(color);
    }

    public static void setViewport(int width, int height) {
        rendererCommand.setViewport(width, height);
    }

    public static void clear() {
        rendererCommand.clear();
    }

    public static void drawIndexed(VertexArray vertexArray) {
        rendererCommand.drawIndexed(vertexArray);
    }

    public static RendererCommand.API getApi() {
        return rendererCommand.getApi();
    }
}
