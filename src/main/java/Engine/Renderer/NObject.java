package Engine.Renderer;

import static org.lwjgl.opengl.GL45.*;

public class NObject extends Object {
    public NObject() {
        super(Mode.TRIANGLES);
        vertices = new float[]{-0.5f, 0.15f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,    // v0/
                -0.15f, 0.15f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,    // v1/
                0.0f, 0.55f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,     // v2/
                0.15f, 0.15f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,     // v3/
                0.5f, 0.15f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,     // v4/
                0.2f, -0.1f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,     // v5/
                0.3f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,     // v6/
                0.0f, -0.3f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,     // v7/
                -0.3f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,    // v8/
                -0.2f, -0.1f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,    // v9/
        };
        indices = new int[]{0, 1, 9, 1, 2, 3, 3, 4, 5, 5, 6, 7, 7, 8, 9, 9, 5, 7, 9, 1, 3, 9, 3, 5};
    }

    @Override
    void init() {
        // Generate and bind a Vertex Array Object
        vaoID = glCreateVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glCreateBuffers();
        int vertexBindingIndex = 0;
        glNamedBufferData(vboID, vertices, GL_STATIC_DRAW);
        glVertexArrayVertexBuffer(vaoID, vertexBindingIndex, vboID, 0, VERTEX_SIZE_BYTES);

        // Create and upload indices buffer
        eboID = glCreateBuffers();
        glNamedBufferData(eboID, indices, GL_STATIC_DRAW);
        glVertexArrayElementBuffer(vaoID, eboID);

        // Enable the buffer attribute pointers
        int positionIndex = 0;
        glVertexArrayAttribFormat(vaoID, positionIndex, POS_SIZE, GL_FLOAT, false, POS_OFFSET);
        glVertexArrayAttribBinding(vaoID, positionIndex, vertexBindingIndex);
        glEnableVertexArrayAttrib(vaoID, positionIndex);

        int colorIndex = 1;
        glVertexArrayAttribFormat(vaoID, colorIndex, COLOR_SIZE, GL_FLOAT, false, COLOR_OFFSET);
        glVertexArrayAttribBinding(vaoID, colorIndex, vertexBindingIndex);
        glEnableVertexArrayAttrib(vaoID, colorIndex);
    }

    @Override
    void render() {
        glBindVertexArray(vaoID);
        glPointSize(5.0f);
        glDrawElements(getMode(), indices.length, GL_UNSIGNED_INT, 0);
    }

    @Override
    void destroy() {
        glDeleteBuffers(eboID);
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
    }
}
