package GameEngine.Engine.Utils;

import GameEngine.Engine.Core.Input;
import GameEngine.Engine.Events.*;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;
import org.joml.Vector3f;

import static GameEngine.Engine.Utils.KeyCodes.*;

public class OrthographicCameraController {
    private final boolean isRotational;
    private final float rotationSpeed;
    private final OrthographicCamera camera;
    private float zoomLevel;
    private float aspectRatio;
    private float movingSpeed;

    public OrthographicCameraController(float aspectRatio, boolean isRotational) {
        this(aspectRatio, 1.0f, isRotational);
    }

    public OrthographicCameraController(float aspectRatio, float zoomLevel, boolean isRotational) {
        this.zoomLevel = zoomLevel;
        this.camera = new OrthographicCamera(-zoomLevel * aspectRatio, zoomLevel * aspectRatio, -zoomLevel, zoomLevel);
        this.isRotational = isRotational;
        this.aspectRatio = aspectRatio;
        rotationSpeed = 180.0f;
        movingSpeed = zoomLevel;
    }

    public Camera getCamera() {
        return camera;
    }

    public void onUpdate(TimeStep ts) {
        if (Input.isKeyPressed(YH_KEY_D)) {
            camera.translate(new Vector3f( //
                    (float) Math.cos(camera.getRotationInRadians()) * movingSpeed * ts.getSeconds(), //
                    (float) Math.sin(camera.getRotationInRadians()) * movingSpeed * ts.getSeconds(), //
                    0.0f) //
            );
        }

        if (Input.isKeyPressed(YH_KEY_A)) {
            camera.translate(new Vector3f( //
                    (float) Math.cos(camera.getRotationInRadians()) * movingSpeed * ts.getSeconds() * -1, //
                    (float) Math.sin(camera.getRotationInRadians()) * movingSpeed * ts.getSeconds() * -1, //
                    0.0f) //
            );
        }

        if (Input.isKeyPressed(YH_KEY_W)) {
            camera.translate(new Vector3f( //
                    (float) Math.sin(camera.getRotationInRadians()) * movingSpeed * ts.getSeconds() * -1, //
                    (float) Math.cos(camera.getRotationInRadians()) * movingSpeed * ts.getSeconds(), //
                    0.0f) //
            );
        }
        if (Input.isKeyPressed(YH_KEY_S)) {
            camera.translate(new Vector3f( //
                    (float) Math.sin(camera.getRotationInRadians()) * movingSpeed * ts.getSeconds(), //
                    (float) Math.cos(camera.getRotationInRadians()) * movingSpeed * ts.getSeconds() * -1, //
                    0.0f) //
            );
        }

        if (isRotational) {
            if (Input.isKeyPressed(YH_KEY_Q)) {
                camera.rotate(-rotationSpeed * ts.getSeconds());
            }
            if (Input.isKeyPressed(YH_KEY_E)) {
                camera.rotate(rotationSpeed * ts.getSeconds());
            }
        }
    }

    public void onEvent(Event event) {
        new EventDispatcher(event).dispatch(EventType.MouseScrolled, this::onMouseScrolled);
        new EventDispatcher(event).dispatch(EventType.WindowResize, this::onWindowResize);
    }

    public boolean onMouseScrolled(Event e) {
        MouseEvents.MouseScrolledEvent event = (MouseEvents.MouseScrolledEvent) e;
        zoomLevel += (float) event.getyOffset() * -0.1f;
        movingSpeed = zoomLevel = Math.max(zoomLevel, 0.1f);
        camera.setProjectionMatrix(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel);
        return false;
    }

    public boolean onWindowResize(Event e) {
        WindowEvents.WindowResizeEvent event = (WindowEvents.WindowResizeEvent) e;
        resize((float) event.getWidth(), (float) event.getHeight());
        return false;
    }

    public void resize(float width, float height) {
        aspectRatio = width / height;
        camera.setProjectionMatrix(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel);
    }
}
