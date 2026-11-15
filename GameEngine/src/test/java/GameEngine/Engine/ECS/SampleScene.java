package GameEngine.Engine.ECS;

import GameEngine.Engine.ECS.Components.CameraComponent;
import GameEngine.Engine.ECS.Components.SpriteComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;
import GameEngine.Engine.Renderer.Camera.PerspectiveCamera;
import org.joml.Vector3f;
import org.joml.Vector4f;

final class SampleScene {

    private SampleScene() {}

    static Scene create() {
        Scene scene = new Scene("Sample", null);

        Entity player = scene.createEntity("Player");
        TransformComponent transform = player.getComponent(TransformComponent.class);
        transform.translate.set(1.5f, -2.25f, 0.1f);
        transform.size.set(2.0f, 3.0f, 1.0f);
        transform.rotation.set(0.0f, 0.0f, 0.75f);
        player.addComponent(new SpriteComponent(new Vector4f(0.2f, 0.4f, 0.6f, 1.0f)));

        Entity mainCamera = scene.createEntity("Main Camera");
        mainCamera.addComponent(new CameraComponent(
                new OrthographicCamera(
                        new Vector3f(0.0f, 1.0f, 2.0f), new Vector3f(0.0f, 0.0f, 0.5f), 1.6f, -1.0f, 1.0f, 3.0f),
                true));

        Entity eye = scene.createEntity("Eye");
        eye.addComponent(new CameraComponent(
                new PerspectiveCamera(
                        new Vector3f(4.0f, 5.0f, 6.0f), new Vector3f(0.1f, 0.2f, 0.3f), 1.25f, 0.1f, 100.0f, 45.0f),
                false));

        return scene;
    }
}
