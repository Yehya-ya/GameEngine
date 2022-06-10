package Platforms.OpenGL;

import Engine.Renderer.RendererCommand;
import Engine.Renderer.VertexArray;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class OpenGLRendererCommand extends RendererCommand {
    @Override
    public void init() {
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
        glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void drawIndexed(@NotNull VertexArray vertexArray) {
        glDrawElements(GL_TRIANGLES, vertexArray.getIndexBuffer().getCount(), GL_UNSIGNED_INT, 0);
    }

    @Override
    public API getApi() {
        return API.OpenGL;
    }
}
