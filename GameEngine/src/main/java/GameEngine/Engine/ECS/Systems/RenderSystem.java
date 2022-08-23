package GameEngine.Engine.ECS.Systems;

import GameEngine.Engine.ECS.Components.SpriteComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;
import org.jetbrains.annotations.NotNull;


@All({TransformComponent.class, SpriteComponent.class})
public class RenderSystem extends EntityProcessingSystem {
    private final Scene scene;
    private ComponentMapper<TransformComponent> transformComponentsMapper;
    private ComponentMapper<SpriteComponent> spriteComponentsMapper;

    public RenderSystem(Scene scene) {
        this.scene = scene;
    }

    @Override
    protected void begin() {
        if (scene.getCamera() != null) {
            BatchRenderer2D.begin(scene.getCamera());
        }
    }

    @Override
    protected void process(@NotNull Entity e) {
        if (scene.getCamera() != null) {
            TransformComponent transform = transformComponentsMapper.get(e);
            SpriteComponent spriteComponent = spriteComponentsMapper.get(e);

            if (spriteComponent.texture != null) {
                BatchRenderer2D.drawRotatedQuad(transform.translate, transform.size, transform.rotation, spriteComponent.texture, spriteComponent.tilingFactor);
            } else {
                BatchRenderer2D.drawRotatedQuad(transform.translate, transform.size, transform.rotation, spriteComponent.color);
            }
        }
    }

    @Override
    protected void end() {
        if (scene.getCamera() != null) {
            BatchRenderer2D.end();
        }
    }
}
