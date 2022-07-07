package GameEngine.Engine.ECS;

import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Systems.CameraSystem;
import GameEngine.Engine.ECS.Systems.RenderSystem;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Utils.TimeStep;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import org.jetbrains.annotations.NotNull;

public class Scene {
    private final World engine;
    private Camera camera;

    public Scene() {
        WorldConfiguration configuration = new WorldConfigurationBuilder() //
                .with(//
                        new TagManager(), //
                        new CameraSystem(this), //
                        new RenderSystem(this) //
                ).build();
        engine = new World(configuration);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public TagManager getTagManager() {
        return engine.getSystem(TagManager.class);
    }

    public Entity createEntity() {
        return createEntity("untitled");
    }

    public Entity createEntity(String name) {
        com.artemis.Entity e = engine.createEntity();
        Entity entity = new Entity(e);
        TagManager tagManager = engine.getSystem(TagManager.class);
        String tmpName = name;
        int i = 1;
        while (tagManager.isRegistered(tmpName)) {
            tmpName = name + i++;
        }
        tagManager.register(tmpName, e);
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
