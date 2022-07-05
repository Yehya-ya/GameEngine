package GameEngine.Engine.ECS;

import GameEngine.Engine.ECS.Components.CameraComponent;
import GameEngine.Engine.ECS.Components.SpriteComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import com.artemis.BaseEntitySystem;
import com.artemis.annotations.One;

@One({CameraComponent.class, SpriteComponent.class, TransformComponent.class})
public class EcsSystem extends BaseEntitySystem {
    @Override
    protected void processSystem() {
    }
}
