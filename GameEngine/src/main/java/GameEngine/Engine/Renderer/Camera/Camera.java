package GameEngine.Engine.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Camera {
    protected Matrix4f projectionMatrix;
    protected Matrix4f viewMatrix;
    protected Matrix4f viewProjectionMatrix;

    protected Vector3f position;
    protected Vector3f rotation;

    protected float aspectRatio;
    protected float far, near;

    public Camera() {
        this(new Matrix4f(), new Matrix4f(), new Matrix4f(), new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(), 1.0f, 1.0f, -1.0f);
    }

    public Camera(Matrix4f projectionMatrix, Matrix4f viewMatrix, Matrix4f viewProjectionMatrix, Vector3f position, Vector3f rotation, float aspectRatio, float far, float near) {
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
        this.viewProjectionMatrix = viewProjectionMatrix;
        this.position = position;
        this.rotation = rotation;
        this.aspectRatio = aspectRatio;
        this.far = far;
        this.near = near;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        recalculateViewMatrix();
        recalculateViewProjectionMatrix();
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        recalculateProjectionMatrix();
        recalculateViewProjectionMatrix();
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
        recalculateProjectionMatrix();
        recalculateViewProjectionMatrix();
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
        recalculateProjectionMatrix();
        recalculateViewProjectionMatrix();
    }

    public void translate(Vector3f offset) {
        this.position.add(offset);
        recalculateViewMatrix();
        recalculateViewProjectionMatrix();
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        recalculateViewMatrix();
        recalculateViewProjectionMatrix();
    }

    protected abstract void recalculateViewMatrix();

    protected abstract void recalculateProjectionMatrix();

    protected abstract void recalculateViewProjectionMatrix();
}
