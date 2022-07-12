package GameEngine.Engine.ECS.Systems;

import GameEngine.Engine.ECS.Components.CameraComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.Renderer.Camera.Camera;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;

@All({CameraComponent.class, TransformComponent.class})
public class CameraSystem extends EntityProcessingSystem {
    private final Scene scene;
    private ComponentMapper<CameraComponent> cameraComponentsMapper;
    private ComponentMapper<TransformComponent> transformComponentsMapper;
    private Camera mainCamera;

    public CameraSystem(Scene scene) {
        this.scene = scene;
    }

    @Override
    protected void begin() {
        mainCamera = null;
    }

    @Override
    protected void process(Entity e) {
        CameraComponent cameraComponent = cameraComponentsMapper.get(e);
        if (cameraComponent.primary) {
            mainCamera = cameraComponent.camera;
        }
        TransformComponent transformComponent = transformComponentsMapper.get(e);
        cameraComponent.camera.setPosition(transformComponent.translate);
        cameraComponent.camera.setRotation(transformComponent.rotation);
    }

    @Override
    protected void end() {
        scene.setCamera(mainCamera);
    }
}
