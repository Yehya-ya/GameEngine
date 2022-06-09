package Platforms.OpenGL;

import Engine.Renderer.Buffer.IndexBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class OpenGLIndexBuffer extends IndexBuffer {
    private final int rendererId;
    private final int count;

    public OpenGLIndexBuffer(int[] indices) {
        this.rendererId = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.rendererId);
        glBufferData(GL_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        this.count = indices.length;
    }

    @Override
    public void delete() {
        glDeleteBuffers(rendererId);
    }

    @Override
    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, rendererId);
    }

    @Override
    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public int getCount() {
        return this.count;
    }
}
