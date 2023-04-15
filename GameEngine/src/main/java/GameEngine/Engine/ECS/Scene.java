package GameEngine.Engine.ECS;

import GameEngine.Engine.Core.Input;
import GameEngine.Engine.ECS.Components.TagComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Systems.CameraSystem;
import GameEngine.Engine.ECS.Systems.RenderSystem;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Events.EventDispatcher;
import GameEngine.Engine.Events.EventType;
import GameEngine.Engine.Events.KeyEvent;
import GameEngine.Engine.Renderer.Buffer.FrameBuffer;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Renderer.Camera.EditorCamera;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Utils.TimeStep;
import GameEngine.Engine.Utils.YH_Log;
import com.artemis.*;
import com.artemis.utils.IntBag;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static GameEngine.Engine.Utils.KeyCodes.*;

public class Scene {
    private final World engine;
    private final String title;
    private final FrameBuffer frameBuffer;
    private final EditorCamera editorCamera;
    private Camera camera;
    private Entity selectedEntity;
    private String uri;
    private Boolean isRuntime;
    private int imguizmoType;


    public Scene() {
        this("Scene", null);
    }

    public Scene(String title, String uri) {
        WorldConfiguration configuration = new WorldConfigurationBuilder() //
                .with(//
                        new CameraSystem(this), //
                        new RenderSystem(this) //
                ).build();
        engine = new World(configuration);
        selectedEntity = null;
        this.title = title;
        this.uri = uri;
        this.editorCamera = new EditorCamera();
        this.isRuntime = false;
        this.imguizmoType = -1;

        FrameBuffer.FrameBufferSpecification specification = new FrameBuffer.FrameBufferSpecification();
        specification.width = 1280;
        specification.height = 720;
        this.frameBuffer = FrameBuffer.create(specification);
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

    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public Entity getEntity(int id) {
        return new Entity(engine.getEntity(id));
    }

    @SafeVarargs
    public final IntBag getEntitiesIds(Class<? extends Component>... args) {
        return engine.getAspectSubscriptionManager().get(Aspect.all(args)).getEntities();
    }

    public Camera getCamera() {
        return isRuntime ? camera : editorCamera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Boolean getRuntime() {
        return isRuntime;
    }

    public void setRuntime(Boolean runtime) {
        isRuntime = runtime;
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
        if (entity == selectedEntity) {
            selectedEntity = null;
        }
        engine.delete(entity.getId());
    }

    public void onUpdate(@NotNull TimeStep timeStep) {
        frameBuffer.bind();
        RendererCommandAPI.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();

        if (!isRuntime) {
            editorCamera.onUpdate(timeStep);
        }
        engine.setDelta(timeStep.getSeconds());
        engine.process();

        frameBuffer.unbind();
    }

    public void onEvent(Event event) {
        (new EventDispatcher(event)).dispatch(EventType.KeyPressed, this::onKeyPressed);
        if (!isRuntime) {
            editorCamera.onEvent(event);
        }
    }

    public boolean onKeyPressed(Event e) {
        KeyEvent.KeyPressedEvent event = (KeyEvent.KeyPressedEvent) e;
        switch (event.getKeyCode()) {
            case YH_KEY_Q -> imguizmoType = -1;
            case YH_KEY_W -> imguizmoType = Operation.TRANSLATE;
            case YH_KEY_E -> imguizmoType = Operation.SCALE;
            case YH_KEY_R -> imguizmoType = Operation.ROTATE;
        }
        return true;
    }

    public void onImgRender() {
        int textureID = frameBuffer.getColorAttachmentRendererId();
        ImVec2 size = ImGui.getContentRegionAvail();

        ImGui.image(textureID, size.x, size.y, 0.0f, 1.0f, 1.0f, 0.0f);

        if (selectedEntity != null && imguizmoType != -1) {
            ImGuizmo.setOrthographic(false);
            ImGuizmo.setDrawList();
            ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());

            Camera mainCamera = getCamera();
            float[] projection = new float[16];
            mainCamera.getProjectionMatrix().get(projection);
            float[] view = new float[16];
            mainCamera.getViewMatrix().get(view);
            TransformComponent transformComponent = selectedEntity.getComponent(TransformComponent.class);
            float[] transformation = new float[16];
            ImGuizmo.recomposeMatrixFromComponents(transformation, new float[]{transformComponent.translate.x, transformComponent.translate.y, transformComponent.translate.z}, new float[]{(float) Math.toDegrees(transformComponent.rotation.x), (float) Math.toDegrees(transformComponent.rotation.y), (float) Math.toDegrees(transformComponent.rotation.z)}, new float[]{transformComponent.size.x, transformComponent.size.y, transformComponent.size.z});

            boolean snap = Input.isKeyPressed(YH_KEY_LEFT_CONTROL);
            float snapValue = 0.5f;
            if (imguizmoType == Operation.ROTATE) {
                snapValue = 45.0f;
            }

            float[] snapValues = {snapValue, snapValue, snapValue};
//            YH_Log.YH_LOG_DEBUG("view: {}", view);
//            YH_Log.YH_LOG_DEBUG("projection: {}", projection);
            YH_Log.YH_LOG_DEBUG("transformation: {}", transformation);
            if (snap) {
                ImGuizmo.manipulate(view, projection, transformation, imguizmoType, Mode.LOCAL, snapValues);
            } else {
                ImGuizmo.manipulate(view, projection, transformation, imguizmoType, Mode.LOCAL);
            }
            if (ImGuizmo.isUsing()) {
                float[] translate = new float[3];
                float[] rotation = new float[3];
                float[] scale = new float[3];
                ImGuizmo.decomposeMatrixToComponents(transformation, translate, rotation, scale);
                transformComponent.translate.set(translate);
                transformComponent.rotation.set(Math.toRadians(rotation[0]), Math.toRadians(rotation[1]), Math.toRadians(rotation[2]));
                transformComponent.size.set(scale);
            }
        }
    }

    public void setViewportSize(Vector2f viewportSize) {
        if (!viewportSize.equals(frameBuffer.getSpecification().width, frameBuffer.getSpecification().height)) {
            frameBuffer.resize((int) viewportSize.x, (int) viewportSize.y);
        }
        if (!isRuntime) {
            editorCamera.setViewportSize(viewportSize);
        } else {
            camera.setAspectRatio(viewportSize.x / viewportSize.y);
        }
    }
}
