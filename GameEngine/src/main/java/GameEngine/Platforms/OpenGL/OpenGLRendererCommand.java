package GameEngine.Platforms.OpenGL;

import GameEngine.Engine.Renderer.RendererCommand;
import GameEngine.Engine.Renderer.VertexArray;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import static GameEngine.Engine.Utils.YH_Log.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.memByteBuffer;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class OpenGLRendererCommand extends RendererCommand {
    @Override
    public void init() {
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            switch (severity) {
                case GL_DEBUG_SEVERITY_HIGH, GL_DEBUG_SEVERITY_MEDIUM -> {
                    YH_LOG_ERROR(memUTF8(memByteBuffer(message, length)));
                    return;
                }
                case GL_DEBUG_SEVERITY_LOW -> {
                    YH_LOG_WARN(memUTF8(memByteBuffer(message, length)));
                    return;
                }
                case GL_DEBUG_SEVERITY_NOTIFICATION -> {
                    YH_LOG_TRACE(memUTF8(memByteBuffer(message, length)));
                    return;
                }
            }

            YH_ASSERT(false, "Unknown severity level!");
        }, 0);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
    public void drawIndexed(@NotNull VertexArray vertexArray) {
        glDrawElements(GL_TRIANGLES, vertexArray.getIndexBuffer().getCount(), GL_UNSIGNED_INT, 0);
    }

    @Override
    public void drawIndexed(int count) {
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
    }

    @Override
    public API getApi() {
        return API.OpenGL;
    }
}
