package GameEngine.Platforms.OpenGL;

import static GameEngine.Engine.Utils.YH_Log.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.memByteBuffer;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import GameEngine.Engine.Renderer.RendererCommand;
import GameEngine.Engine.Renderer.VertexArray;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public class OpenGLRendererCommand extends RendererCommand {
    private static final int nvidiaBufferMemoryInfoId = 131185;

    @Override
    public void init() {
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback(
                (source, type, id, severity, length, message, userParam) ->
                        logDebugMessage(type, severity, id, memUTF8(memByteBuffer(message, length))),
                0);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private static void logDebugMessage(int type, int severity, int id, String message) {
        if (type == GL_DEBUG_TYPE_PERFORMANCE) {
            YH_LOG_DEBUG("Id: {}, {}", id, message);
            return;
        }
        switch (severity) {
            case GL_DEBUG_SEVERITY_HIGH, GL_DEBUG_SEVERITY_MEDIUM -> YH_LOG_ERROR("Id: {}, {}", id, message);
            case GL_DEBUG_SEVERITY_LOW -> YH_LOG_WARN("Id: {}, {}", id, message);
            case GL_DEBUG_SEVERITY_NOTIFICATION -> {
                if (id != nvidiaBufferMemoryInfoId) {
                    YH_LOG_TRACE("Id: {}, {}", id, message);
                }
            }
            default -> YH_ASSERT(false, "Unknown severity level!");
        }
    }

    @Override
    public void setClearColor(@NotNull Vector4f color) {
        glClearColor(color.x, color.y, color.z, color.w);
    }

    @Override
    public void setViewport(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void drawIndexed(@NotNull VertexArray vertexArray, int count) {
        vertexArray.bind();
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
    }

    @Override
    public API getApi() {
        return API.OpenGL;
    }
}
