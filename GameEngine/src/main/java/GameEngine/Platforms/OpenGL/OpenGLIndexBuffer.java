package GameEngine.Platforms.OpenGL;

import GameEngine.Engine.Renderer.Buffer.IndexBuffer;
import org.jetbrains.annotations.NotNull;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class OpenGLIndexBuffer extends IndexBuffer {
    private final int rendererId;
    private final int count;

    public OpenGLIndexBuffer(int @NotNull [] indices) {
        YH_LOG_TRACE("Creating OpenGLIndexBuffer with {} indices.", indices.length);
        this.rendererId = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.rendererId);
        glBufferData(GL_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        this.count = indices.length;
    }

    @Override
    public void delete() {
        glDeleteBuffers(rendererId);
        YH_LOG_TRACE("Deleting OpenGLIndexBuffer");
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
