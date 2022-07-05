package GameEngine.Engine.ECS;

import GameEngine.Engine.ECS.Components.SpriteComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;
import org.jetbrains.annotations.NotNull;


@All({TransformComponent.class, SpriteComponent.class})
public class RenderSystem extends EntityProcessingSystem {
    ComponentMapper<TransformComponent> transformComponentsMapper;
    ComponentMapper<SpriteComponent> spriteComponentsMapper;

    @Override
    protected void process(@NotNull Entity e) {
        TransformComponent transform = transformComponentsMapper.get(e);
        SpriteComponent spriteComponent = spriteComponentsMapper.get(e);

        if (spriteComponent.texture != null) {
            BatchRenderer2D.drawRotatedQuad(transform.transform, transform.size, transform.rotationAngle, spriteComponent.texture, spriteComponent.tilingFactor);
        } else {
            BatchRenderer2D.drawRotatedQuad(transform.transform, transform.size, transform.rotationAngle, spriteComponent.color);
        }
    }
}
