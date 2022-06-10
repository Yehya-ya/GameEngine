package Engine.Renderer.Camera;

import org.joml.Matrix4f;

public class OrthographicCamera extends Camera {
    public OrthographicCamera(float left, float right, float bottom, float top) {
        viewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f().ortho(left, right, bottom, top, -1.0f, 1.0f);
        viewProjectionMatrix = new Matrix4f().mul(projectionMatrix).mul(viewMatrix);
    }

    public void setProjectionMatrix(float left, float right, float bottom, float top) {
        projectionMatrix = new Matrix4f().ortho(left, right, bottom, top, -1.0f, 1.0f);
        viewProjectionMatrix = new Matrix4f().mul(projectionMatrix).mul(viewMatrix);
    }
}
