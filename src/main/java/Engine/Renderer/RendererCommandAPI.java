package Engine.Renderer;

import Platforms.OpenGL.OpenGLRendererCommand;
import org.joml.Vector4f;

public class RendererCommandAPI {
    private static final RendererCommand RENDERER_COMMAND = new OpenGLRendererCommand();

    private RendererCommandAPI() {
    }

    public static void SetClearColor(Vector4f color) {
        RENDERER_COMMAND.setClearColor(color);
    }

    public static void clear() {
        RENDERER_COMMAND.clear();
    }

    public static void drawIndexed(VertexArray vertexArray) {
        RENDERER_COMMAND.drawIndexed(vertexArray);
    }

    public static RendererCommand.API getApi() {
        return RENDERER_COMMAND.getApi();
    }
}
