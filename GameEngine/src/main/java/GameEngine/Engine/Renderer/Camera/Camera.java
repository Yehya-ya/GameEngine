package GameEngine.Engine.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Camera {
    protected Matrix4f projectionMatrix;
    protected Matrix4f viewMatrix;
    protected Vector3f position;
    protected Vector3f rotation;
    protected float aspectRatio;
    protected float far, near;

    public Camera() {
        this(new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(), 1.0f, 0.1f, 1000.0f);
    }

    public Camera(Vector3f position, Vector3f rotation, float aspectRatio, float near, float far) {
        this.position = position;
        this.rotation = rotation;
        this.aspectRatio = aspectRatio;
        this.near = near;
        this.far = far;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getViewProjectionMatrix() {
        return new Matrix4f().mul(projectionMatrix).mul(viewMatrix);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        updateViewMatrix();
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        updateProjectionMatrix();
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
        updateProjectionMatrix();
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
        updateProjectionMatrix();
    }

    public void translate(Vector3f offset) {
        this.position.add(offset);
        updateViewMatrix();
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        updateViewMatrix();
    }

    protected abstract void updateViewMatrix();

    protected abstract void updateProjectionMatrix();
}
