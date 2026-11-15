package GameEngine.Engine.ECS;

import GameEngine.Engine.ECS.Components.*;
import GameEngine.Engine.ECS.Systems.CameraSystem;
import GameEngine.Engine.ECS.Systems.RenderSystem;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Utils.TimeStep;
import com.artemis.*;
import com.artemis.utils.IntBag;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class Scene {
    private final World engine;
    private final String title;
    private String uri;
    private Camera editorCamera;
    private Camera primaryCamera;
    private BatchRenderer2D renderer;
    private boolean isRunning;

    public Scene() {
        this("Scene", null);
    }

    public Scene(String title, String uri) {
        WorldConfiguration configuration = new WorldConfigurationBuilder()
                .with(new CameraSystem(this), new RenderSystem(this))
                .build();
        engine = new World(configuration);
        this.title = title;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Entity getEntity(int id) {
        return new Entity(engine.getEntity(id));
    }

    @SafeVarargs
    public final IntBag getEntitiesIds(Class<? extends Component>... args) {
        return engine.getAspectSubscriptionManager().get(Aspect.all(args)).getEntities();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public BatchRenderer2D getRenderer() {
        return renderer;
    }

    public Camera getRenderCamera() {
        return isRunning ? primaryCamera : editorCamera;
    }

    public void setPrimaryCamera(Camera primaryCamera) {
        this.primaryCamera = primaryCamera;
    }

    public Entity createEntity() {
        return createEntity("untitled");
    }

    public Entity createEntity(String name) {
        return createEntityWithUuid(name, UUID.randomUUID());
    }

    public Entity createEntityWithUuid(String name, UUID uuid) {
        com.artemis.Entity e = engine.createEntity();
        Entity entity = new Entity(e);
        entity.addComponent(new TagComponent(name));
        entity.addComponent(new IdComponent(uuid));
        entity.addComponent(new TransformComponent());
        return entity;
    }

    public Entity emptyEntity() {
        com.artemis.Entity e = engine.createEntity();
        Entity entity = new Entity(e);
        return entity;
    }

    public void delete() {
        engine.dispose();
    }

    public void deleteEntity(@NotNull Entity entity) {
        engine.delete(entity.getId());
    }

    public void onUpdateEditor(@NotNull TimeStep timeStep, Camera editorCamera, @NotNull BatchRenderer2D renderer) {
        this.renderer = renderer;
        this.editorCamera = editorCamera;
        isRunning = false;
        process(timeStep);
    }

    public void onUpdateRuntime(@NotNull TimeStep timeStep, @NotNull BatchRenderer2D renderer) {
        this.renderer = renderer;
        isRunning = true;
        process(timeStep);
    }

    public void onViewportResize(float width, float height) {
        IntBag cameraEntities = getEntitiesIds(CameraComponent.class);
        for (int i = 0; i < cameraEntities.size(); i++) {
            Camera camera = getEntity(cameraEntities.get(i)).getComponent(CameraComponent.class).camera;
            camera.setAspectRatio(width / height);
        }
    }

    private void process(@NotNull TimeStep timeStep) {
        engine.setDelta(timeStep.getSeconds());
        engine.process();
    }

    private Component copyComponent(Component component) {
        return ComponentTypes.of(component).copy(component);
    }

    private Component duplicateComponent(Component component) {
        if (component instanceof IdComponent) {
            return new IdComponent();
        }
        if (component instanceof TagComponent tagComponent) {
            return new TagComponent(tagComponent.name + " copy");
        }
        return copyComponent(component);
    }

    public Scene copy() {
        Scene copy = new Scene(title, null);

        IntBag entitiesIds = getEntitiesIds(IdComponent.class);
        for (int i = 0; i < entitiesIds.size(); i++) {
            Entity source = getEntity(entitiesIds.get(i));
            Entity target = copy.emptyEntity();
            for (Component component : source.getAllComponents()) {
                target.addComponent(copyComponent(component));
            }
        }
        return copy;
    }

    public void duplicateEntity(Entity srcEntity) {
        Entity disEntity = emptyEntity();
        for (Component component : srcEntity.getAllComponents()) {
            disEntity.addComponent(duplicateComponent(component));
        }
    }
}
