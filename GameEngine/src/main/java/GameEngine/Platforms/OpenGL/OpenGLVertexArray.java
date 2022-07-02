package GameEngine.Platforms.OpenGL;

import GameEngine.Engine.Renderer.Buffer.BufferLayout;
import GameEngine.Engine.Renderer.Buffer.IndexBuffer;
import GameEngine.Engine.Renderer.Buffer.VertexBuffer;
import GameEngine.Engine.Renderer.VertexArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Vector;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;


public class OpenGLVertexArray extends VertexArray {
    private final int rendererId;
    private final Vector<VertexBuffer> vertexBuffers;
    private int vertexBufferIndex;
    private IndexBuffer indexBuffer;

    public OpenGLVertexArray() {
        YH_LOG_TRACE("Creating OpenGLVertexArray.");

        vertexBuffers = new Vector<>();
        this.rendererId = glCreateVertexArrays();
        vertexBufferIndex = 0;
    }

    @Override
    public void delete() {
        indexBuffer.delete();
        for (VertexBuffer vertexBuffer : vertexBuffers) {
            vertexBuffer.delete();
        }
        glDeleteVertexArrays(this.rendererId);
        YH_LOG_TRACE("Deleting OpenGLVertexArray.");
    }

    @Override
    public void bind() {
        glBindVertexArray(this.rendererId);
    }

    @Override
    public void unbind() {
        glBindVertexArray(0);
    }

    @Override
    public void addVertexBuffer(@NotNull VertexBuffer vertexBuffer) {
        YH_ASSERT(vertexBuffer.getLayout().getBufferElements().size() != 0, "Vertex Buffer has no layout!");
        glBindVertexArray(rendererId);
        vertexBuffer.bind();

        for (BufferLayout.BufferElement element : vertexBuffer.getLayout().getBufferElements()) {
            glEnableVertexAttribArray(vertexBufferIndex);
            glVertexAttribPointer(vertexBufferIndex, element.GetComponentCount(), getOpenGLNativeType(element.type), element.normalized, vertexBuffer.getLayout().getOffset(), element.offset);
            vertexBufferIndex++;
        }

        vertexBuffers.add(vertexBuffer);
    }

    @Override
    public Vector<VertexBuffer> getVertexBuffers() {
        return vertexBuffers;
    }

    @Override
    public IndexBuffer getIndexBuffer() {
        return indexBuffer;
    }

    @Override
    public void setIndexBuffer(@NotNull IndexBuffer indexBuffer) {
        glBindVertexArray(rendererId);
        indexBuffer.bind();

        this.indexBuffer = indexBuffer;
    }

    @Contract(pure = true)
    private int getOpenGLNativeType(BufferLayout.@NotNull ShaderDataType type) {
        switch (type) {
            case Float:
            case Mat4:
            case Mat3:
            case Float2:
            case Float3:
            case Float4:
                return GL_FLOAT;
            case Int:
            case Int2:
            case Int4:
            case Int3:
                return GL_INT;
            case Bool:
                return GL_BOOL;
        }

        YH_ASSERT(false, "Unknown ShaderDataType!");
        return 0;
    }
}
