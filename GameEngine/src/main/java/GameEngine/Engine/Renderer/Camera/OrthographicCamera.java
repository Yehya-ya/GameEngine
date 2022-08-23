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
        recalculateViewMatrix();
        recalculateProjectionMatrix();
        recalculateViewProjectionMatrix();
    }

    public OrthographicCamera(Matrix4f projectionMatrix, Matrix4f viewMatrix, Matrix4f viewProjectionMatrix, Vector3f position, Vector3f rotation, float aspectRatio, float near, float far, float zoomLevel) {
        super(projectionMatrix, viewMatrix, viewProjectionMatrix, position, rotation, aspectRatio, near, far);
        this.zoomLevel = zoomLevel;
        recalculateViewMatrix();
        recalculateProjectionMatrix();
        recalculateViewProjectionMatrix();
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
        recalculateProjectionMatrix();
        recalculateViewProjectionMatrix();
    }

    public void setProjectionMatrix(float aspectRatio, float zoomLevel) {
        this.aspectRatio = aspectRatio;
        this.zoomLevel = zoomLevel;
        recalculateProjectionMatrix();
        recalculateViewProjectionMatrix();
    }

    @Override
    protected void recalculateViewMatrix() {
        Matrix4f transform = new Matrix4f().translate(position).rotateAffineXYZ(rotation.x, rotation.y, rotation.z);
        viewMatrix = transform.invert();
    }

    @Override
    protected void recalculateProjectionMatrix() {
        projectionMatrix = new Matrix4f().ortho(aspectRatio * zoomLevel * -0.5f, aspectRatio * zoomLevel * 0.5f, zoomLevel * -0.5f, zoomLevel * 0.5f, near, far);
    }

    @Override
    protected void recalculateViewProjectionMatrix() {
        viewProjectionMatrix = new Matrix4f().mul(projectionMatrix).mul(viewMatrix);
    }
}
