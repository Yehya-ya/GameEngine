package GameEngine.Engine.Renderer.Camera;

import GameEngine.Engine.Core.Input;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Events.EventDispatcher;
import GameEngine.Engine.Events.EventType;
import GameEngine.Engine.Events.MouseEvents;
import GameEngine.Engine.Utils.KeyCodes;
import GameEngine.Engine.Utils.MouseCodes;
import GameEngine.Engine.Utils.TimeStep;
import org.joml.Math;
import org.joml.*;

public class EditorCamera extends Camera {
    private final float fov;
    private final Vector3f focalPoint;
    private Vector2f initialMousePosition;
    private float distance = 10.0f;
    private float pitch = 0.0f;
    private float yaw = 0.0f;
    private float viewportWidth = 1280;
    private float viewportHeight = 720;

    public EditorCamera() {
        this(1.778f, 45.0f, 0.1f, 1000.0f);
    }

    public EditorCamera(float aspectRatio, float fov, float nearClip, float farClip) {
        super();
        this.position = new Vector3f();
        this.focalPoint = new Vector3f();
        this.initialMousePosition = new Vector2f();
        this.near = nearClip;
        this.far = farClip;
        this.aspectRatio = aspectRatio;
        this.fov = fov;
        updateViewMatrix();
        updateProjectionMatrix();
    }

    public float getDistance() {
        return distance;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    private Vector2f panSpeed() {
        float x = Math.min(viewportWidth / 1000.0f, 2.4f); // max = 2.4f
        float xFactor = 0.0366f * (x * x) - 0.1778f * x + 0.3021f;

        float y = Math.min(viewportHeight / 1000.0f, 2.4f); // max = 2.4f
        float yFactor = 0.0366f * (y * y) - 0.1778f * y + 0.3021f;

        return new Vector2f(xFactor, yFactor);
    }

    private float rotationSpeed() {
        return 0.8f;
    }

    private float zoomSpeed() {
        float distance = this.distance * 0.2f;
        distance = Math.max(distance, 0.0f);
        float speed = distance * distance;
        speed = Math.min(speed, 100.0f); // max speed = 100
        return speed;
    }

    public void onUpdate(TimeStep ts) {
        if (Input.isKeyPressed(KeyCodes.YH_KEY_LEFT_ALT)) {
            Vector2f mouse = new Vector2f((float) Input.getMouseX(), (float) Input.getMouseY());
            Vector2f delta = new Vector2f(mouse).sub(initialMousePosition).mul(0.003f);
            initialMousePosition = mouse;

            if (Input.isMouseButtonPressed(MouseCodes.GLFW_MOUSE_BUTTON_MIDDLE)) {
                mousePan(delta);
            } else if (Input.isMouseButtonPressed(MouseCodes.GLFW_MOUSE_BUTTON_LEFT)) {
                mouseRotate(delta);
            } else if (Input.isMouseButtonPressed(MouseCodes.GLFW_MOUSE_BUTTON_RIGHT)) {
                mouseZoom(delta.y);
            }
        }

        updateViewMatrix();
    }

    public void onEvent(Event event) {
        (new EventDispatcher(event)).dispatch(EventType.MouseScrolled, this::onMouseScroll);
    }

    public boolean onMouseScroll(Event e) {
        MouseEvents.MouseScrolledEvent event = (MouseEvents.MouseScrolledEvent) e;
        float delta = (float) (event.getyOffset() * 0.1f);
        mouseZoom(delta);
        updateViewMatrix();
        return false;
    }

    private void mousePan(Vector2f delta) {
        Vector2f speed = panSpeed();
        focalPoint.add(getRightDirection().mul(-1).mul(delta.x).mul(speed.x).mul(distance));
        focalPoint.add(getUpDirection().mul(delta.y).mul(speed.y).mul(distance));
    }

    void mouseRotate(Vector2f delta) {
        float yawSign = getUpDirection().y < 0 ? -1.0f : 1.0f;
        yaw += (yawSign * delta.x * rotationSpeed());
        pitch += (delta.y * rotationSpeed());
    }

    void mouseZoom(float delta) {
        distance -= delta * zoomSpeed();
        if (distance < 1.0f) {
            focalPoint.add(getForwardDirection());
            distance = 1.0f;
        }
    }

    private Vector3f getUpDirection() {
        return new Vector3f(0.0f, 1.0f, 0.0f).rotate(getOrientation());
    }

    private Vector3f getRightDirection() {
        return new Vector3f(1.0f, 0.0f, 0.0f).rotate(getOrientation());
    }

    private Vector3f getForwardDirection() {
        return new Vector3f(0.0f, 0.0f, -1.0f).rotate(getOrientation());
    }

    private Vector3f calculatePosition() {
        return new Vector3f(focalPoint).sub(getForwardDirection().mul(distance));
    }

    private Quaternionf getOrientation() {
        return new Quaternionf(-pitch, -yaw, 0.0f, 1.0f);
    }

    public void setViewportSize(float width, float height) {
        viewportWidth = width;
        viewportHeight = height;
        updateProjectionMatrix();
    }

    public void setViewportSize(Vector2f viewportSize) {
        this.setViewportSize(viewportSize.x, viewportSize.y);
    }

    @Override
    protected void updateViewMatrix() {
        position = calculatePosition();
        Quaternionf orientation = getOrientation();
        viewMatrix = new Matrix4f().translate(position).mul(new Matrix4f().set(orientation));
        viewMatrix = viewMatrix.invert();
    }

    @Override
    protected void updateProjectionMatrix() {
        aspectRatio = viewportWidth / viewportHeight;
        projectionMatrix = new Matrix4f().perspective(Math.toRadians(fov), aspectRatio, near, far);
    }
}
