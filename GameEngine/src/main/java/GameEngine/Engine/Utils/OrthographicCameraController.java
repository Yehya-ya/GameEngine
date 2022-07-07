package GameEngine.Engine.Utils;

import GameEngine.Engine.Events.*;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;

public class OrthographicCameraController {
    private final OrthographicCamera camera;
    private float zoomLevel;
    private float aspectRatio;

    public OrthographicCameraController(float aspectRatio) {
        this(aspectRatio, 1.0f);
    }

    public OrthographicCameraController(float aspectRatio, float zoomLevel) {
        this.zoomLevel = zoomLevel;
        this.camera = new OrthographicCamera(aspectRatio, zoomLevel);
        this.aspectRatio = aspectRatio;
    }

    public Camera getCamera() {
        return camera;
    }

    public void onUpdate(TimeStep ts) {

    }

    public void onEvent(Event event) {
        new EventDispatcher(event).dispatch(EventType.MouseScrolled, this::onMouseScrolled);
        new EventDispatcher(event).dispatch(EventType.WindowResize, this::onWindowResize);
    }

    public boolean onMouseScrolled(Event e) {
        MouseEvents.MouseScrolledEvent event = (MouseEvents.MouseScrolledEvent) e;
        zoomLevel += (float) event.getyOffset() * -0.1f;
        camera.setProjectionMatrix(aspectRatio, zoomLevel);
        return false;
    }

    public boolean onWindowResize(Event e) {
        WindowEvents.WindowResizeEvent event = (WindowEvents.WindowResizeEvent) e;
        resize((float) event.getWidth(), (float) event.getHeight());
        return false;
    }

    public void resize(float width, float height) {
        aspectRatio = width / height;
        camera.setProjectionMatrix(aspectRatio, zoomLevel);
    }
}
