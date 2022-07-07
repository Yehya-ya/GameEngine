package GameEngine.Engine.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PerspectiveCamera extends Camera {
    private float fov;

    public PerspectiveCamera(float aspectRatio) {
        this(aspectRatio, 45.0f);
    }

    public PerspectiveCamera(float aspectRatio, float fov) {
        super();
        this.near = 0.01f;
        this.far = 1000.f;
        this.aspectRatio = aspectRatio;
        this.fov = fov;
        recalculateViewMatrix();
        recalculateProjectionMatrix();
        recalculateViewProjectionMatrix();
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
        recalculateProjectionMatrix();
        recalculateViewProjectionMatrix();
    }

    @Override
    protected void recalculateViewMatrix() {
        viewMatrix = new Matrix4f().lookAt(position, new Vector3f(position.x, position.y, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f).rotateZ((float) Math.toRadians(rotation)));
    }

    @Override
    protected void recalculateProjectionMatrix() {
        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(fov), aspectRatio, near, far);
    }

    @Override
    protected void recalculateViewProjectionMatrix() {
        viewProjectionMatrix = new Matrix4f().mul(projectionMatrix).mul(viewMatrix);
    }
}
