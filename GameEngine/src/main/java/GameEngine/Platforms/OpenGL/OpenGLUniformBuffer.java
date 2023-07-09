package GameEngine.Platforms.OpenGL;

import GameEngine.Engine.Renderer.UniformBuffer;

import static org.lwjgl.opengl.GL45.*;

public class OpenGLUniformBuffer extends UniformBuffer {
    private final int rendererId;

    public OpenGLUniformBuffer(long size, int binding) {
        rendererId = glCreateBuffers();
        glNamedBufferData(rendererId, size, GL_DYNAMIC_DRAW); // TODO: investigate usage hint
        glBindBufferBase(GL_UNIFORM_BUFFER, binding, rendererId);
    }

    @Override
    public void delete() {
        glDeleteBuffers(rendererId);
    }

    @Override
    public void setData(float[] data, long offset) {
        glNamedBufferSubData(rendererId, offset, data);
    }
}
