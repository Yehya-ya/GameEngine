package Engine.Renderer;

import Engine.Scenes.Scene;
import Engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL40.*;

public class CirclesOnSquare extends Object {
    private Vector3f scale;
    private Vector2f point1, point2;

    public CirclesOnSquare(Scene scene) {
        super(scene, "vertexShader", "circles");
        vertices = new float[]{ //
                -1f, -1f,//v0
                1f, -1f, //v1
                1f, 1f,  //v2
                -1f, 1f, //v3
        };
        indices = new int[]{ //
                0, 1, 2, //t1
                0, 2, 3 //t2
        };

        scale = new Vector3f(Window.getWidth(), Window.getHeight(), 1.0f);
        point1 = new Vector2f(0.7f,0.6f);
        point2 = new Vector2f(0.3f,0.4f);
    }

    @Override
    public void init() {
        super.init();
        int vertexSize = 2;
        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, vertexSize, GL_FLOAT, false, vertexSize * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        shaderProgram.bind();
        shaderProgram.setVector2f("point1", point1);
        shaderProgram.setVector2f("point2", point2);
        shaderProgram.setVector2f("u_resolution", new Vector2f(Window.getWidth(),Window.getHeight()));
        shaderProgram.unbind();
    }

    @Override
    public void update(float dt) {
        transformationMatrix = new Matrix4f().scale(scale);

        shaderProgram.bind();
        shaderProgram.setFloat("u_time", (float) glfwGetTime());
        shaderProgram.unbind();
    }

    public void updateAR() {
        scale = new Vector3f(Window.getWidth(), Window.getHeight(), 1.0f);
        shaderProgram.bind();
        shaderProgram.setVector2f("u_resolution", new Vector2f(Window.getWidth(),Window.getHeight()));
        shaderProgram.unbind();
    }
}
