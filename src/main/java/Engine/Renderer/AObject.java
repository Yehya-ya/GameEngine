package Engine.Renderer;

import static org.lwjgl.opengl.GL45.*;

public class AObject extends Object {
    public AObject() {
        super(Mode.TRIANGLES);
        vertices = new float[]{
                -0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, //v1
                0.5f, -0.5f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, //v1
                0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, //v1
                -0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, //v1
                0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, //v1
                -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, //v1
        };
    }

    @Override
    void init() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    @Override
    void render() {
        glBindVertexArray(vaoID);
        glPointSize(5.0f);
        glDrawArrays(getMode(), 0, vertices.length / VERTEX_SIZE);
    }

    @Override
    void destroy() {
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
    }
}
