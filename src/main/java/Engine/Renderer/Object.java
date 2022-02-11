package Engine.Renderer;

import Engine.Scenes.Scene;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL45.*;

public abstract class Object {
    protected final ShaderProgram shaderProgram;
    private final Scene scene;
    protected int vaoID, vboID, eboID;
    protected float[] vertices;
    protected int[] indices;
    protected Matrix4f transformationMatrix;


    public Object(Scene scene, String vertexShader, String fragmentShader) {
        this.scene = scene;
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        transformationMatrix = new Matrix4f().identity();
    }

    public void init() {
        shaderProgram.compileAndLink();

        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    abstract public void update(float dt);

    public void render() {
        shaderProgram.bind();
        shaderProgram.setMatrix4f("projectionMatrix", scene.getProjectionMatrix());
        shaderProgram.setMatrix4f("viewMatrix", scene.getViewMatrix());
        shaderProgram.setMatrix4f("transformationMatrix", transformationMatrix);
        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        shaderProgram.unbind();
    }

    public void destroy() {
        glDeleteBuffers(eboID);
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
        shaderProgram.destroy();
    }
}
