package Platforms.OpenGL;

import Engine.Renderer.Buffer.BufferLayout;
import Engine.Renderer.Buffer.IndexBuffer;
import Engine.Renderer.Buffer.VertexBuffer;
import Engine.Renderer.VertexArray;

import java.util.Vector;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;


public class OpenGLVertexArray extends VertexArray {
    private final int rendererId;
    private final Vector<VertexBuffer> vertexBuffers;
    private IndexBuffer indexBuffer;

    public OpenGLVertexArray() {
        vertexBuffers = new Vector<>();
        this.rendererId = glCreateVertexArrays();
    }

    public void delete() {
        glDeleteVertexArrays(this.rendererId);
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
    public void addVertexBuffer(VertexBuffer vertexBuffer) {
        YH_ASSERT(vertexBuffer.getLayout().getBufferElements().size() != 0, "Vertex Buffer has no layout!");
        glBindVertexArray(rendererId);
        vertexBuffer.bind();

        int index = 0;
        for (BufferLayout.BufferElement element : vertexBuffer.getLayout().getBufferElements()) {
            glEnableVertexAttribArray(index);
            glVertexAttribPointer(index,
                    element.GetComponentCount(),
                    getOpenGLNativeType(element.type),
                    element.normalized,
                    vertexBuffer.getLayout().getOffset(),
                    element.offset);
            index++;
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
    public void setIndexBuffer(IndexBuffer indexBuffer) {
        glBindVertexArray(rendererId);
        indexBuffer.bind();

        this.indexBuffer = indexBuffer;
    }

    private int getOpenGLNativeType(BufferLayout.ShaderDataType type) {
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
