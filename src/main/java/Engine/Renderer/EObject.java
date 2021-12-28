package Engine.Renderer;

import static org.lwjgl.opengl.GL45.*;

public class EObject extends Object {
    public EObject() {
        super(Mode.LINES);
        vertices = new float[]{
                -0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,//v1
                0.5f, -0.5f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,//v1
                0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,//v1
                -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,//v1
        };
        indices = new int[]{
                0, 1, //line1
                1, 2, //line1
                2, 0, //line1
                2, 3, //line1
                3, 0, //line1
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

        // Create and upload indices buffer
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

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
        glDrawElements(getMode(), indices.length, GL_UNSIGNED_INT, 0);
    }

    @Override
    void destroy() {
        glDeleteBuffers(eboID);
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
    }
}
