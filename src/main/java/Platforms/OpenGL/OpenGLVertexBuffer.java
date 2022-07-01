package Platforms.OpenGL;

import Engine.Renderer.Buffer.VertexBuffer;
import org.jetbrains.annotations.NotNull;

import static Engine.Utils.YH_Log.YH_LOG_TRACE;
import static org.lwjgl.opengl.GL45.*;

public class OpenGLVertexBuffer extends VertexBuffer {
    private final int rendererId;

    public OpenGLVertexBuffer(long size) {
        YH_LOG_TRACE("Creating OpenGLVertexBuffer empty with {} byte size.", size);
        rendererId = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, rendererId);
        glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
        YH_LOG_TRACE("size: {}", size);
    }

    public OpenGLVertexBuffer(float @NotNull [] vertices) {
        YH_LOG_TRACE("Creating OpenGLVertexBuffer with {} vertices.", vertices.length);
        rendererId = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, rendererId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
    }

    @Override
    public void setData(float[] data) {
        glBindBuffer(GL_ARRAY_BUFFER, rendererId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
    }

    @Override
    public void delete() {
        glDeleteBuffers(rendererId);
        YH_LOG_TRACE("Deleting OpenGLVertexBuffer.");
    }

    @Override
    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, rendererId);
    }

    @Override
    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
