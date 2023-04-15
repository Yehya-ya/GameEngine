package GameEngine.Engine.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class OrthographicCamera extends Camera {
    private float zoomLevel;

    public OrthographicCamera(float aspectRatio) {
        this(aspectRatio, 1.0f);
    }

    public OrthographicCamera(float aspectRatio, float zoomLevel) {
        this(aspectRatio, zoomLevel, 0.01f, 1000.0f);
    }

    public OrthographicCamera(float aspectRatio, float zoomLevel, float near, float far) {
        super();
        this.aspectRatio = aspectRatio;
        this.zoomLevel = zoomLevel;
        this.near = near;
        this.far = far;
        updateViewMatrix();
        updateProjectionMatrix();
    }

    public OrthographicCamera(Vector3f position, Vector3f rotation, float aspectRatio, float near, float far, float zoomLevel) {
        super(position, rotation, aspectRatio, near, far);
        this.zoomLevel = zoomLevel;
        updateViewMatrix();
        updateProjectionMatrix();
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
        updateProjectionMatrix();
    }

    public void setProjectionMatrix(float aspectRatio, float zoomLevel) {
        this.aspectRatio = aspectRatio;
        this.zoomLevel = zoomLevel;
        updateProjectionMatrix();
    }

    @Override
    protected void updateViewMatrix() {
        Matrix4f transform = new Matrix4f().translate(position).rotateAffineXYZ(rotation.x, rotation.y, rotation.z);
        viewMatrix = transform.invert();
    }

    @Override
    protected void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f().ortho(aspectRatio * zoomLevel * -0.5f, aspectRatio * zoomLevel * 0.5f, zoomLevel * -0.5f, zoomLevel * 0.5f, near, far);
    }
}
