package Platforms.OpenGL;

import Engine.Renderer.Buffer.VertexBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class OpenGLVertexBuffer extends VertexBuffer {
    private final int rendererId;

    public OpenGLVertexBuffer(float[] vertices) {
        this.rendererId = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.rendererId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
    }

    @Override
    public void delete() {
        glDeleteBuffers(rendererId);
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
