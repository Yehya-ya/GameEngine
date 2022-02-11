package Engine.Scenes;

import Engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Scene {

    protected float aspectRatio;
    protected float far;
    protected float near;
    protected float fov;
    protected Matrix4f projectionMatrix;
    protected Matrix4f viewMatrix;

    public Scene() {
        far = 1000.0f;
        near = 0.1f;
        fov = 45.0f;
        aspectRatio = (float) Window.getWidth() / (float) Window.getHeight();
        projectionMatrix = new Matrix4f().ortho(-Window.getWidth()/2, Window.getWidth()/2, -Window.getHeight()/2, Window.getHeight()/2, near, far);
        viewMatrix = new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f,10.0f), new Vector3f(0.0f,0.0f,0.0f), new Vector3f(0.0f,1.0f,0.0f));
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public void init() {

    }

    public abstract void update(float dt);

    public void updateAspectRatio() {
        aspectRatio = (float) Window.getWidth() / (float) Window.getHeight();
        projectionMatrix = new Matrix4f().ortho(-Window.getWidth()/2, Window.getWidth()/2, -Window.getHeight()/2, Window.getHeight()/2, near, far);
    }
}
