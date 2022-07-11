package GameEngine.Engine.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class OrthographicCamera extends Camera {
    private float zoomLevel;

    public OrthographicCamera(float aspectRatio) {
        this(aspectRatio, 1.0f);
    }

    public OrthographicCamera(float aspectRatio, float zoomLevel) {
        super();
        this.aspectRatio = aspectRatio;
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
        Vector3f rotation = getRotationInRadians();
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
