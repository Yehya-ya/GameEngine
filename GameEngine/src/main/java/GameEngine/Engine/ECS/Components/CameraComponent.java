package GameEngine.Engine.ECS.Components;

import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Renderer.Camera.CameraType;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;

public class CameraComponent extends com.artemis.Component {
    public Camera camera;
    public boolean primary;
    public CameraType cameraType;

    public CameraComponent() {
        this(null, false);
    }

    public CameraComponent(Camera camera) {
        this(camera, false);
    }

    public CameraComponent(Camera camera, boolean primary) {
        if (camera instanceof OrthographicCamera) {
            cameraType = CameraType.Orthographic;
        } else {
            cameraType = CameraType.Perspective;
        }
        this.camera = camera;
        this.primary = primary;
    }
}
