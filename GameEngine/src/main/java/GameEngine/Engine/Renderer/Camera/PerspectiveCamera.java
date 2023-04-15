package GameEngine.Engine.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PerspectiveCamera extends Camera {
    private float fov;

    public PerspectiveCamera(float aspectRatio) {
        this(aspectRatio, 45.0f);
    }

    public PerspectiveCamera(float aspectRatio, float fov) {
        this(aspectRatio, fov, 0.01f, 1000.0f);
    }

    public PerspectiveCamera(float aspectRatio, float fov, float near, float far) {
        super();
        this.near = near;
        this.far = far;
        this.aspectRatio = aspectRatio;
        this.fov = fov;
        updateViewMatrix();
        updateProjectionMatrix();
    }

    public PerspectiveCamera(Vector3f position, Vector3f rotation, float aspectRatio, float near, float far, float fov) {
        super(position, rotation, aspectRatio, near, far);
        this.fov = fov;
        updateViewMatrix();
        updateProjectionMatrix();
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
        updateProjectionMatrix();
    }

    @Override
    protected void updateViewMatrix() {
        viewMatrix = new Matrix4f().translate(position).rotateAffineXYZ(rotation.x, rotation.y, rotation.z).invert();
    }

    @Override
    protected void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(fov), aspectRatio, near, far);
    }
}
