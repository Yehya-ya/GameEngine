package GameEngine.Engine.ECS;

import GameEngine.Engine.Renderer.BatchRenderer2D;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Utils.TimeStep;
import com.artemis.*;
import org.jetbrains.annotations.NotNull;

public class Scene {
    private final World engine;
    private final Camera camera;

    public  Scene(Camera camera) {
        WorldConfiguration configuration = new WorldConfigurationBuilder()
                .with(new EcsSystem(),new RenderSystem())
                .build();
        engine = new World(configuration);
        this.camera = camera;
    }

    public Entity createEntity() {
        com.artemis.Entity entity = engine.createEntity();
        return new Entity(entity);
    }

    public void deleteEntity(@NotNull Entity entity) {
        engine.delete(entity.getId());
    }

    public void onUpdate(@NotNull TimeStep timeStep) {
        BatchRenderer2D.begin(camera);
        engine.setDelta(timeStep.getSeconds());
        engine.process();
        BatchRenderer2D.end();
    }
}
