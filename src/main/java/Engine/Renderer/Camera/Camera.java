package Engine.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Camera {
    protected Matrix4f projectionMatrix;
    protected Matrix4f viewMatrix;
    protected Matrix4f viewProjectionMatrix;

    protected Vector3f position;
    protected int rotation;

    public Camera() {
        position = new Vector3f();
        rotation = 0;
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
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
        recalculateViewMatrix();
    }

    private void recalculateViewMatrix() {
        Matrix4f transform = new Matrix4f().translate(position).rotateZ((float) Math.toRadians(rotation));
        viewMatrix = transform.invert();
        viewProjectionMatrix = new Matrix4f().mul(projectionMatrix).mul(viewMatrix);
    }
}
