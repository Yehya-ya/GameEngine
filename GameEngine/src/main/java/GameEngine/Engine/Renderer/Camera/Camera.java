package GameEngine.Engine.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Camera {
    protected Matrix4f projectionMatrix;
    protected Matrix4f viewMatrix;
    protected Matrix4f viewProjectionMatrix;

    protected Vector3f position;
    protected float rotation;

    protected float aspectRatio;
    protected float far, near;

    public Camera() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        viewProjectionMatrix = new Matrix4f();
        position = new Vector3f(0.0f, 0.0f, 1.0f);
        rotation = 0.0f;
        aspectRatio = 1.0f;
        far = 1.0f;
        near = -1.0f;
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

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        recalculateViewMatrix();
        recalculateViewProjectionMatrix();
    }

    public float getRotationInRadians() {
        return (float) Math.toRadians(rotation);
    }

    public void rotate(float angle) {
        this.rotation += angle;
        recalculateViewMatrix();
        recalculateViewProjectionMatrix();
    }

    protected abstract void recalculateViewMatrix();

    protected abstract void recalculateProjectionMatrix();

    protected abstract void recalculateViewProjectionMatrix();
}
