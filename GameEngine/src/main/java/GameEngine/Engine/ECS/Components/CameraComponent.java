package GameEngine.Engine.ECS.Components;

import GameEngine.Engine.Renderer.Camera.Camera;

public class CameraComponent extends com.artemis.Component {
    public Camera camera;
    public boolean primary;

    public CameraComponent() {
        this(null, false);
    }

    public CameraComponent(Camera camera) {
        this(camera, false);
    }

    public CameraComponent(Camera camera, boolean primary) {
        this.camera = camera;
        this.primary = primary;
    }
}
