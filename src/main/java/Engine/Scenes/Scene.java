package Engine.Scenes;

import Engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Scene {

    protected Matrix4f projectionMatrix;
    protected Matrix4f viewMatrix;
    protected Camera camera;

    public Scene() {
        this.camera = new Camera(new Vector3f(20.0f,15.0f, 0.0f));
        this.camera.setFov(70.0f);
        this.updateAspectRatio();
        this.projectionMatrix = new Matrix4f().perspective(this.camera.getFov(), this.camera.getAspectRatio(), this.camera.getNear(), this.camera.getFar());
        this.viewMatrix = new Matrix4f().lookAt(this.camera.getPos(), this.camera.getLookAt(), this.camera.getUp());
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public void init() {

    }

    public void update(float dt){
        this.projectionMatrix = new Matrix4f().perspective(this.camera.getFov(), this.camera.getAspectRatio(), this.camera.getNear(), this.camera.getFar());
        this.viewMatrix = new Matrix4f().lookAt(this.camera.getPos(), this.camera.getLookAt(), this.camera.getUp());
    }

    public void updateAspectRatio() {
        this.camera.setAspectRatio((float) Window.getWidth()/ (float) Window.getHeight());
    }
}
