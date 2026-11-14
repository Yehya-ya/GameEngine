import static GameEngine.Engine.Utils.KeyCodes.*;

import GameEngine.Engine.Core.Input;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Entity;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Events.EventDispatcher;
import GameEngine.Engine.Events.EventType;
import GameEngine.Engine.Events.KeyEvent;
import GameEngine.Engine.Events.MouseEvents;
import GameEngine.Engine.Renderer.Buffer.FrameBuffer;
import GameEngine.Engine.Renderer.Camera.EditorCamera;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Utils.MouseCodes;
import GameEngine.Engine.Utils.TimeStep;
import Panels.Selection;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Viewport {
    private static final int entityIdAttachment = 1;
    private static final int noEntity = -1;
    private static final int noGizmo = -1;
    private static final float translateAndScaleSnap = 0.5f;
    private static final float rotateSnapDegrees = 45.0f;
    private static final Vector4f clearColor = new Vector4f(0.1f, 0.1f, 0.1f, 1);

    private final Selection selection;
    private final FrameBuffer frameBuffer;
    private final Vector2f boundsMin = new Vector2f();
    private final Vector2f boundsMax = new Vector2f();
    private EditorCamera editorCamera;
    private Scene scene;
    private int gizmoOperation;
    private boolean isHovered;

    public Viewport(Selection selection) {
        this.selection = selection;
        this.editorCamera = new EditorCamera();
        this.gizmoOperation = noGizmo;

        FrameBuffer.FrameBufferSpecification specification = new FrameBuffer.FrameBufferSpecification();
        specification.width = 1280;
        specification.height = 720;
        specification.attachments = new FrameBuffer.FramebufferAttachmentSpecification();
        specification.attachments.addAttachments(
                new FrameBuffer.FramebufferTextureSpecification(FrameBuffer.FramebufferTextureFormat.RGBA8));
        specification.attachments.addAttachments(
                new FrameBuffer.FramebufferTextureSpecification(FrameBuffer.FramebufferTextureFormat.RED_INTEGER));
        specification.attachments.addAttachments(
                new FrameBuffer.FramebufferTextureSpecification(FrameBuffer.FramebufferTextureFormat.Depth));
        this.frameBuffer = FrameBuffer.create(specification);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        editorCamera = new EditorCamera();
    }

    public void delete() {
        frameBuffer.delete();
    }

    public void resize(@NotNull Vector2f size) {
        FrameBuffer.FrameBufferSpecification specification = frameBuffer.getSpecification();
        if (!size.equals(specification.width, specification.height)) {
            frameBuffer.resize((int) size.x, (int) size.y);
        }
        editorCamera.setViewportSize(size);
    }

    public void onUpdate(@NotNull TimeStep timeStep) {
        frameBuffer.bind();
        RendererCommandAPI.setClearColor(clearColor);
        RendererCommandAPI.clear();
        frameBuffer.clearAttachment(entityIdAttachment, noEntity);

        editorCamera.onUpdate(timeStep);
        scene.onUpdateEditor(timeStep, editorCamera);

        frameBuffer.unbind();
    }

    public void onEvent(Event event) {
        new EventDispatcher(event).dispatch(EventType.KeyPressed, this::onKeyPressed);
        new EventDispatcher(event).dispatch(EventType.MouseButtonPressed, this::onMousePressed);
        editorCamera.onEvent(event);
    }

    public void onImGuiRender() {
        isHovered = ImGui.isWindowHovered();
        drawImage();
        Entity selectedEntity = selection.getEntity();
        if (selectedEntity != null && gizmoOperation != noGizmo) {
            drawGizmo(selectedEntity);
        }
    }

    private boolean onKeyPressed(Event e) {
        KeyEvent.KeyPressedEvent event = (KeyEvent.KeyPressedEvent) e;
        if (ImGuizmo.isUsing()) {
            return true;
        }
        switch (event.getKeyCode()) {
            case YH_KEY_Q -> gizmoOperation = noGizmo;
            case YH_KEY_W -> gizmoOperation = Operation.TRANSLATE;
            case YH_KEY_E -> gizmoOperation = Operation.SCALE;
            case YH_KEY_R -> gizmoOperation = Operation.ROTATE;
        }
        return true;
    }

    private boolean onMousePressed(Event e) {
        MouseEvents.MouseButtonPressedEvent event = (MouseEvents.MouseButtonPressedEvent) e;
        boolean isSelectClick = event.getButton() == MouseCodes.GLFW_MOUSE_BUTTON_LEFT
                && isHovered
                && !ImGuizmo.isOver()
                && !Input.isKeyPressed(YH_KEY_LEFT_ALT);
        if (isSelectClick) {
            selectEntityUnderMouse();
        }
        return true;
    }

    private void selectEntityUnderMouse() {
        ImVec2 mouse = ImGui.getMousePos();
        Vector2f size = new Vector2f(boundsMax).sub(boundsMin);
        int mouseX = (int) (mouse.x - boundsMin.x);
        int mouseY = (int) (size.y - (mouse.y - boundsMin.y));
        boolean isInsideImage = mouseX >= 0 && mouseY >= 0 && mouseX < (int) size.x && mouseY < (int) size.y;
        if (isInsideImage) {
            selectEntityAt(mouseX, mouseY);
        }
    }

    private void selectEntityAt(int x, int y) {
        frameBuffer.bind();
        int entityId = frameBuffer.ReadPixel(entityIdAttachment, x, y);
        frameBuffer.unbind();
        if (entityId == noEntity) {
            selection.clear();
        } else {
            selection.select(scene.getEntity(entityId));
        }
    }

    private void drawImage() {
        ImVec2 size = ImGui.getContentRegionAvail();
        ImGui.image(frameBuffer.getColorAttachmentRendererId(), size.x, size.y, 0.0f, 1.0f, 1.0f, 0.0f);
        ImVec2 contentMin = ImGui.getWindowContentRegionMin();
        ImVec2 contentMax = ImGui.getWindowContentRegionMax();
        ImVec2 windowPosition = ImGui.getWindowPos();
        boundsMin.set(contentMin.x + windowPosition.x, contentMin.y + windowPosition.y);
        boundsMax.set(contentMax.x + windowPosition.x, contentMax.y + windowPosition.y);
    }

    private void drawGizmo(Entity selectedEntity) {
        ImGuizmo.setOrthographic(false);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(boundsMin.x, boundsMin.y, boundsMax.x - boundsMin.x, boundsMax.y - boundsMin.y);

        float[] projection = new float[16];
        editorCamera.getProjectionMatrix().get(projection);
        float[] view = new float[16];
        editorCamera.getViewMatrix().get(view);
        TransformComponent transformComponent = selectedEntity.getComponent(TransformComponent.class);
        float[] transformation = new float[16];
        ImGuizmo.recomposeMatrixFromComponents(
                transformation,
                new float[] {
                    transformComponent.translate.x, transformComponent.translate.y, transformComponent.translate.z
                },
                new float[] {
                    (float) Math.toDegrees(transformComponent.rotation.x),
                    (float) Math.toDegrees(transformComponent.rotation.y),
                    (float) Math.toDegrees(transformComponent.rotation.z)
                },
                new float[] {transformComponent.size.x, transformComponent.size.y, transformComponent.size.z});

        boolean snap = Input.isKeyPressed(YH_KEY_LEFT_CONTROL) || Input.isKeyPressed(YH_KEY_RIGHT_CONTROL);
        float snapValue = gizmoOperation == Operation.ROTATE ? rotateSnapDegrees : translateAndScaleSnap;
        float[] snapValues = {snapValue, snapValue, snapValue};
        if (snap) {
            ImGuizmo.manipulate(view, projection, transformation, gizmoOperation, Mode.LOCAL, snapValues);
        } else {
            ImGuizmo.manipulate(view, projection, transformation, gizmoOperation, Mode.LOCAL);
        }
        if (ImGuizmo.isUsing()) {
            float[] translate = new float[3];
            float[] rotation = new float[3];
            float[] scale = new float[3];
            ImGuizmo.decomposeMatrixToComponents(transformation, translate, rotation, scale);
            transformComponent.translate.set(translate);
            transformComponent.rotation.set(
                    Math.toRadians(rotation[0]), Math.toRadians(rotation[1]), Math.toRadians(rotation[2]));
            transformComponent.size.set(scale);
        }
    }
}
