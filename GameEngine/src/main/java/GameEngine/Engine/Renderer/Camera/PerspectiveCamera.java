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

    public PerspectiveCamera(Matrix4f projectionMatrix, Matrix4f viewMatrix, Matrix4f viewProjectionMatrix, Vector3f position, Vector3f rotation, float aspectRatio, float far, float near, float fov) {
        super(projectionMatrix, viewMatrix, viewProjectionMatrix, position, rotation, aspectRatio, far, near);
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
        viewMatrix = new Matrix4f().translate(position).rotateAffineXYZ(rotation.x, rotation.y, rotation.z).invert();
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
