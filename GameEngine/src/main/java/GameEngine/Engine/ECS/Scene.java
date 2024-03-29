package GameEngine.Engine.ECS;

import GameEngine.Engine.Core.Input;
import GameEngine.Engine.ECS.Components.*;
import GameEngine.Engine.ECS.Systems.CameraSystem;
import GameEngine.Engine.ECS.Systems.RenderSystem;
import GameEngine.Engine.Events.*;
import GameEngine.Engine.Renderer.Buffer.FrameBuffer;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Renderer.Camera.EditorCamera;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;
import GameEngine.Engine.Renderer.Camera.PerspectiveCamera;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Utils.MouseCodes;
import GameEngine.Engine.Utils.TimeStep;
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
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.UUID;

import static GameEngine.Engine.Utils.KeyCodes.*;

public class Scene {
    private final World engine;
    private final String title;
    private final FrameBuffer frameBuffer;
    private final EditorCamera editorCamera;
    private final Vector2f[] viewportBounds;
    private Camera camera;
    private Entity selectedEntity;
    private String uri;
    private Boolean isRuntime;
    private int imguizmoType;
    private int hoveredEntityId;
    private boolean viewportHovered;
    private Vector2f viewportSize;
    private Vector2f mousePos;


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
        this.viewportBounds = new Vector2f[]{new Vector2f(), new Vector2f()};

        FrameBuffer.FrameBufferSpecification specification = new FrameBuffer.FrameBufferSpecification();
        specification.width = 1280;
        specification.height = 720;
        specification.attachments = new FrameBuffer.FramebufferAttachmentSpecification();
        specification.attachments.addAttachments(new FrameBuffer.FramebufferTextureSpecification(FrameBuffer.FramebufferTextureFormat.RGBA8));
        specification.attachments.addAttachments(new FrameBuffer.FramebufferTextureSpecification(FrameBuffer.FramebufferTextureFormat.RED_INTEGER));
        specification.attachments.addAttachments(new FrameBuffer.FramebufferTextureSpecification(FrameBuffer.FramebufferTextureFormat.Depth));
        this.frameBuffer = FrameBuffer.create(specification);

        viewportSize = new Vector2f();
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
        frameBuffer.clearAttachment(1, -1);

        if (!isRuntime) {
            editorCamera.onUpdate(timeStep);
        }
        engine.setDelta(timeStep.getSeconds());
        engine.process();

        ImVec2 vec2 = ImGui.getMousePos();
        vec2.x -= viewportBounds[0].x;
        vec2.y -= viewportBounds[0].y;
        Vector2f viewportSize = new Vector2f(viewportBounds[1]).sub(viewportBounds[0]);
        vec2.y = viewportSize.y - vec2.y;

        mousePos = new Vector2f(vec2.x, vec2.y);

        frameBuffer.unbind();
    }

    public void onEvent(Event event) {
        (new EventDispatcher(event)).dispatch(EventType.KeyPressed, this::onKeyPressed);
        (new EventDispatcher(event)).dispatch(EventType.MouseButtonPressed, this::onMousePressed);
        if (!isRuntime) {
            editorCamera.onEvent(event);
        }
    }

    public boolean onKeyPressed(Event e) {
        KeyEvent.KeyPressedEvent event = (KeyEvent.KeyPressedEvent) e;
        boolean control = Input.isKeyPressed(YH_KEY_LEFT_CONTROL) || Input.isKeyPressed(YH_KEY_RIGHT_CONTROL);
        switch (event.getKeyCode()) {
            case YH_KEY_Q -> {
                if (!ImGuizmo.isUsing()) {
                    imguizmoType = -1;
                }
            }
            case YH_KEY_W -> {
                if (!ImGuizmo.isUsing()) {
                    imguizmoType = Operation.TRANSLATE;
                }
            }
            case YH_KEY_E -> {
                if (!ImGuizmo.isUsing()) {
                    imguizmoType = Operation.SCALE;
                }
            }
            case YH_KEY_R -> {
                if (!ImGuizmo.isUsing()) {
                    imguizmoType = Operation.ROTATE;
                }
            }
            case YH_KEY_D -> {
                if (control)
                    duplicateEntity(this.selectedEntity);
            }
        }
        return true;
    }

    public boolean onMousePressed(Event e) {
        MouseEvents.MouseButtonPressedEvent event = (MouseEvents.MouseButtonPressedEvent) e;
        if (event.getButton() == MouseCodes.GLFW_MOUSE_BUTTON_LEFT) {
            if (viewportHovered && !ImGuizmo.isOver() && !Input.isKeyPressed(YH_KEY_LEFT_ALT)) {
                int mouseX = (int) mousePos.x;
                int mouseY = (int) mousePos.y;
                if (mouseX >= 0 && mouseY >= 0 && mouseX < (int) viewportSize.x && mouseY < (int) viewportSize.y) {
                    frameBuffer.bind();
                    hoveredEntityId = frameBuffer.ReadPixel(1, mouseX, mouseY);
                    frameBuffer.unbind();
                    if (hoveredEntityId >= 0) {
                        setSelectedEntity(this.getEntity(hoveredEntityId));
                    } else {
                        setSelectedEntity(null);
                    }
                }
            }
        }
        return true;
    }

    public Component copyComponent(Component component) {
        if (component instanceof IdComponent) {
            return new IdComponent();
        }
        if (component instanceof TagComponent tagComponent) {
            return new TagComponent(tagComponent.name + " copy");
        }
        if (component instanceof TransformComponent transformComponent) {
            return new TransformComponent(
                    new Vector3f(transformComponent.translate),
                    new Vector3f(transformComponent.size),
                    new Vector3f(transformComponent.rotation));
        }
        if (component instanceof SpriteComponent spriteComponent) {
            if (spriteComponent.texture != null) {
                return new SpriteComponent(spriteComponent.texture, spriteComponent.tilingFactor);
            } else {
                return new SpriteComponent(new Vector4f(spriteComponent.color));
            }
        }

        if (component instanceof CameraComponent cameraComponent) {
            if (cameraComponent.camera instanceof OrthographicCamera orthographicCamera) {
                return new CameraComponent(
                        new OrthographicCamera(
                            new Vector3f(orthographicCamera.getPosition()),
                            new Vector3f(orthographicCamera.getRotation()),
                            orthographicCamera.getAspectRatio(),
                            orthographicCamera.getNear(),
                            orthographicCamera.getFar(),
                            orthographicCamera.getZoomLevel()
                        ),
                        cameraComponent.primary);
            } else if (cameraComponent.camera instanceof PerspectiveCamera perspectiveCamera) {
                return new CameraComponent(
                        new PerspectiveCamera(
                                new Vector3f(perspectiveCamera.getPosition()),
                                new Vector3f(perspectiveCamera.getRotation()),
                                perspectiveCamera.getAspectRatio(),
                                perspectiveCamera.getNear(),
                                perspectiveCamera.getFar(),
                                perspectiveCamera.getFov()
                        ), cameraComponent.primary);
            }
        }

        return null;
    }

    public Scene copy(Scene other) {
        Scene newScene = new Scene();
        newScene.setViewportSize(other.viewportSize);

        IntBag entitiesIds = other.getEntitiesIds(IdComponent.class);
        for (int i = 0; i < entitiesIds.size(); i++) {
            Entity srcEntity = other.getEntity(entitiesIds.get(i));
            Entity disEntity = newScene.emptyEntity();
            for (Component component : srcEntity.getAllComponents()) {
                disEntity.addComponent(copyComponent(component));
            }
        }
        return newScene;
    }

    public void duplicateEntity(Entity srcEntity) {
        Entity disEntity = emptyEntity();
        for (Component component : srcEntity.getAllComponents()) {
            disEntity.addComponent(copyComponent(component));
        }
    }

    public void onImgRender() {
        viewportHovered = ImGui.isWindowHovered();

        int textureID = frameBuffer.getColorAttachmentRendererId();
        ImVec2 size = ImGui.getContentRegionAvail();
        ImGui.image(textureID, size.x, size.y, 0.0f, 1.0f, 1.0f, 0.0f);
        ImVec2 viewportMinRegion = ImGui.getWindowContentRegionMin();
        ImVec2 viewportMaxRegion = ImGui.getWindowContentRegionMax();
        ImVec2 viewportOffset = ImGui.getWindowPos();
        viewportBounds[0] = new Vector2f(viewportMinRegion.x + viewportOffset.x, viewportMinRegion.y + viewportOffset.y);
        viewportBounds[1] = new Vector2f(viewportMaxRegion.x + viewportOffset.x, viewportMaxRegion.y + viewportOffset.y);

        if (selectedEntity != null && imguizmoType != -1) {
            ImGuizmo.setOrthographic(false);
            ImGuizmo.setDrawList();
            ImGuizmo.setRect(viewportBounds[0].x, viewportBounds[0].y, viewportBounds[1].x - viewportBounds[0].x, viewportBounds[1].y - viewportBounds[0].y);

            Camera mainCamera = getCamera();
            float[] projection = new float[16];
            mainCamera.getProjectionMatrix().get(projection);
            float[] view = new float[16];
            mainCamera.getViewMatrix().get(view);
            TransformComponent transformComponent = selectedEntity.getComponent(TransformComponent.class);
            float[] transformation = new float[16];
            ImGuizmo.recomposeMatrixFromComponents(transformation, new float[]{transformComponent.translate.x, transformComponent.translate.y, transformComponent.translate.z}, new float[]{(float) Math.toDegrees(transformComponent.rotation.x), (float) Math.toDegrees(transformComponent.rotation.y), (float) Math.toDegrees(transformComponent.rotation.z)}, new float[]{transformComponent.size.x, transformComponent.size.y, transformComponent.size.z});

            boolean snap = Input.isKeyPressed(YH_KEY_LEFT_CONTROL) || Input.isKeyPressed(YH_KEY_RIGHT_CONTROL);
            float snapValue = 0.5f;
            if (imguizmoType == Operation.ROTATE) {
                snapValue = 45.0f;
            }

            float[] snapValues = {snapValue, snapValue, snapValue};
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
        this.viewportSize = viewportSize;
        if (!viewportSize.equals(frameBuffer.getSpecification().width, frameBuffer.getSpecification().height)) {
            frameBuffer.resize((int) viewportSize.x, (int) viewportSize.y);
        }

        if (!isRuntime) {
            editorCamera.setViewportSize(viewportSize);
        } else {
            if (getCamera() != null)
                getCamera().setAspectRatio(viewportSize.x / viewportSize.y);
        }
    }
}
