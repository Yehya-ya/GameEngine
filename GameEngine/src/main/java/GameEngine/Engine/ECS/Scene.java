package GameEngine.Engine.ECS;

import GameEngine.Engine.ECS.Components.TagComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Systems.CameraSystem;
import GameEngine.Engine.ECS.Systems.RenderSystem;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Utils.TimeStep;
import com.artemis.*;
import com.artemis.utils.IntBag;
import org.jetbrains.annotations.NotNull;

public class Scene {
    private final World engine;
    private Camera camera;

    public Scene() {
        WorldConfiguration configuration = new WorldConfigurationBuilder() //
                .with(//
                        new CameraSystem(this), //
                        new RenderSystem(this) //
                ).build();
        engine = new World(configuration);
    }

    public Entity getEntity(int id) {
        return new Entity(engine.getEntity(id));
    }

    @SafeVarargs
    public final IntBag getEntitiesIds(Class<? extends Component>... args) {
        return engine.getAspectSubscriptionManager().get(Aspect.all(args)).getEntities();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Entity createEntity() {
        return createEntity("untitled");
    }

    public Entity createEntity(String name) {
        com.artemis.Entity e = engine.createEntity();
        Entity entity = new Entity(e);
        entity.addComponent(new TagComponent(name));
        entity.addComponent(new TransformComponent());
        return entity;
    }

    public void deleteEntity(@NotNull Entity entity) {
        engine.delete(entity.getId());
    }

    public void onUpdate(@NotNull TimeStep timeStep) {
        engine.setDelta(timeStep.getSeconds());
        engine.process();
    }
}
