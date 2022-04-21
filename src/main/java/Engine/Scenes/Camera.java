package Engine.Scenes;

import org.joml.Vector3f;

public class Camera {
    private Vector3f pos;
    private Vector3f lookAt;
    private Vector3f up;
    private float fov;
    private float near;
    private float far;
    private float aspectRatio;

    public Camera (Vector3f pos) {
        this.pos = pos;
        this.lookAt = new Vector3f(0.0f,0.0f,0.0f);
        this.up = new Vector3f(0.0f,1.0f,0.0f);
        this.fov = 45.0f;
        this.near = 0.1f;
        this.far = 1000.0f;
        this.aspectRatio = 1f/2f;
    }

    public Camera (Vector3f pos ,Vector3f lookAt,Vector3f up) {
        this.pos = pos;
        this.lookAt = lookAt;
        this.up = up;
        this.fov = 45.0f;
        this.near = 0.1f;
        this.far = 10000.0f;
        this.aspectRatio = 1f/2f;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getLookAt() {
        return lookAt;
    }

    public void setLookAt(Vector3f lookAt) {
        this.lookAt = lookAt;
    }

    public Vector3f getUp() {
        return up;
    }

    public void setUp(Vector3f up) {
        this.up = up;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
}
