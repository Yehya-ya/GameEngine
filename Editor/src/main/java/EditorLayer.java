import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Input;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Entity;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.ECS.SceneSerializer;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Events.EventDispatcher;
import GameEngine.Engine.Events.EventType;
import GameEngine.Engine.Events.KeyEvent;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import GameEngine.Engine.Renderer.Buffer.FrameBuffer;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Renderer.RendererStatistics;
import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Utils.TimeStep;
import Panels.SceneHierarchyPanel;
import Utils.FileDialog;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.*;
import imgui.type.ImBoolean;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static GameEngine.Engine.Utils.KeyCodes.*;

public class EditorLayer extends Layer {
    public static final long OpenFileDialogId = 101;
    public static final long SaveFileDialogId = 102;
    private double average;
    private Texture bricks;
    private Texture dirt;
    private FrameBuffer frameBuffer;
    private Vector2f viewportSize;
    private boolean isViewPortIsFocused, isViewPortIsHovered;
    private List<Scene> scenes;
    private Scene activeScene;
    private SceneHierarchyPanel sceneHierarchyPanel;
    private int imguizmoType;

    public EditorLayer() {
        super("Example Layer 2D");
    }

    @Override
    public void onAttach() {
        scenes = new ArrayList<>();
        bricks = Texture.create("assets/textures/bricks.png");
        dirt = Texture.create("assets/textures/dirt.png");
        BatchRenderer2D.init();
        average = 0;

        FrameBuffer.FrameBufferSpecification specification = new FrameBuffer.FrameBufferSpecification();
        specification.width = 1280;
        specification.height = 720;
        frameBuffer = FrameBuffer.create(specification);
        viewportSize = new Vector2f(0);
        isViewPortIsFocused = false;
        isViewPortIsHovered = false;
        imguizmoType = -1;

        sceneHierarchyPanel = new SceneHierarchyPanel();
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        // update
        if (isViewPortIsFocused) {
        }

        if (!viewportSize.equals(frameBuffer.getSpecification().width, frameBuffer.getSpecification().height)) {
            frameBuffer.resize((int) viewportSize.x, (int) viewportSize.y);
        }

        // render
        RendererStatistics.getInstance().reset();
        frameBuffer.bind();
        RendererCommandAPI.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();

        // scene
        if (activeScene != null) {
            activeScene.onUpdate(timeStep);
        }

        frameBuffer.unbind();

        average = average * (30 - 1) / 30 + timeStep.getMilliseconds() / 30;
    }

    @Override
    public void onImgRender() {
        super.onImgRender();

        ImBoolean dockspaceOpen = new ImBoolean(true);
        boolean opt_fullscreen_persistant = true;
        boolean opt_fullscreen = opt_fullscreen_persistant;
        int dockspace_flags = ImGuiDockNodeFlags.None;

        // We are using the ImGuiWindowFlags_NoDocking flag to make the parent window not dockable into,
        // because it would be confusing to have two docking targets within each others.
        int window_flags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        if (opt_fullscreen) {
            ImGuiViewport viewport = ImGui.getMainViewport();
            ImGui.setNextWindowPos(viewport.getPosX(), viewport.getPosY());
            ImGui.setNextWindowSize(viewport.getSizeX(), viewport.getSizeY());
            ImGui.setNextWindowViewport(viewport.getID());
            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
            window_flags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove;
            window_flags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
        }

        // When using ImGuiDockNodeFlags_PassthruCentralNode, DockSpace() will render our background and handle the pass-thru hole, so we ask Begin() to not render a background.
        if ((dockspace_flags & ImGuiDockNodeFlags.PassthruCentralNode) != 0)
            window_flags |= ImGuiWindowFlags.NoBackground;

        // Important: note that we proceed even if Begin() returns false (aka window is collapsed).
        // This is because we want to keep our DockSpace() active. If a DockSpace() is inactive,
        // all active windows docked into it will lose their parent and become undocked.
        // We cannot preserve the docking relationship between an active window and an inactive docking, otherwise
        // any change of dockspace/settings would lead to windows being stuck in limbo and never being visible.
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("DockSpace Demo", dockspaceOpen, window_flags);
        ImGui.popStyleVar();

        if (opt_fullscreen) ImGui.popStyleVar(2);

        // DockSpace
        ImGuiIO io = ImGui.getIO();
        if ((io.getConfigFlags() & ImGuiConfigFlags.DockingEnable) != 0) {
            int dockspace_id = ImGui.getID("MyDockSpace");
            ImGui.dockSpace(dockspace_id, 0.0f, 0.0f, dockspace_flags);
        }

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("New (Ctrl + N)")) {
                    newSceneActivity();
                }
                if (ImGui.menuItem("Open (Ctrl + O)")) {
                    open();
                }
                if (ImGui.menuItem("Save (Ctrl + S)")) {
                    save();
                }
                if (ImGui.menuItem("Save as (Ctrl + Shift + S)")) {
                    saveAs();
                }
                if (ImGui.menuItem("Exit")) {
                    Application.get().close();
                }
                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        FileDialog.update();
        sceneHierarchyPanel.onImGuiRender();

        ImGui.begin("Stats");
        RendererStatistics stats = RendererStatistics.getInstance();
        ImGui.text("Renderer2D Stats:");
        ImGui.text("Draw Calls: " + stats.getDrawCallsCount());
        ImGui.text("Quads: " + stats.getQuadsCount());
        ImGui.textWrapped("Average Rendering time for one frame: " + String.format("%2f", average) + " ms");
        ImGui.end();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport");
        ImGui.beginTabBar("Tabs");
        for (Scene scene: scenes) {
            int tabFlags = ImGuiTabItemFlags.Trailing;

            if (ImGui.beginTabItem(scene.getTitle(), tabFlags)) {
                if (!scene.equals(activeScene)) {
                    setActiveScene(scene);
                }
                ImGui.endTabItem();
            }
        }
        ImGui.endTabBar();

        isViewPortIsFocused = ImGui.isWindowFocused();
        isViewPortIsHovered = ImGui.isWindowHovered();
        Application.get().getImGuiLayer().setBlockingEvents(!isViewPortIsFocused && !isViewPortIsHovered);

        int textureID = frameBuffer.getColorAttachmentRendererId();
        ImVec2 size = ImGui.getContentRegionAvail();
        viewportSize = new Vector2f(size.x, size.y);

        ImGui.image(textureID, size.x, size.y, 0.0f, 1.0f, 1.0f, 0.0f);

        Entity selectedEntity = activeScene != null ? activeScene.getSelectedEntity() : null;
        if (selectedEntity != null && imguizmoType != -1) {
            ImGuizmo.setOrthographic(true);
            ImGuizmo.setDrawList();
            ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());

            Camera mainCamera = activeScene.getCamera();
            float[] projection = new float[16];
            projection = mainCamera.getProjectionMatrix().get(projection);
            float[] view = new float[16];
            view = mainCamera.getViewMatrix().get(view);
            TransformComponent transformComponent = selectedEntity.getComponent(TransformComponent.class);
            float[] transformation = new float[16];
            ImGuizmo.recomposeMatrixFromComponents(transformation, new float[]{transformComponent.translate.x, transformComponent.translate.y, transformComponent.translate.z}, new float[]{(float) Math.toDegrees(transformComponent.rotation.x), (float) Math.toDegrees(transformComponent.rotation.y), (float) Math.toDegrees(transformComponent.rotation.z)}, new float[]{transformComponent.size.x, transformComponent.size.y, transformComponent.size.z});

            boolean snap = Input.isKeyPressed(YH_KEY_LEFT_CONTROL);
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

        ImGui.end();
        ImGui.popStyleVar();

        ImGui.end();
    }

    @Override
    public void onDetach() {
        BatchRenderer2D.shutdown();
        bricks.delete();
        dirt.delete();
    }

    @Override
    public void onEvent(Event event) {
        (new EventDispatcher(event)).dispatch(EventType.KeyPressed, this::onKeyPressed);
    }

    private boolean onKeyPressed(Event e) {
        KeyEvent.KeyPressedEvent event = (KeyEvent.KeyPressedEvent) e;

        // shortcuts
        if (event.repeatCount > 0) {
            return false;
        }

        boolean control = Input.isKeyPressed(YH_KEY_LEFT_CONTROL) || Input.isKeyPressed(YH_KEY_RIGHT_CONTROL);
        boolean shift = Input.isKeyPressed(YH_KEY_LEFT_SHIFT) || Input.isKeyPressed(YH_KEY_RIGHT_SHIFT);

        switch (event.getKeyCode()) {
            case YH_KEY_Q -> imguizmoType = -1;
            case YH_KEY_W -> imguizmoType = Operation.TRANSLATE;
            case YH_KEY_E -> imguizmoType = Operation.SCALE;
            case YH_KEY_R -> imguizmoType = Operation.ROTATE;
            case YH_KEY_S -> {
                if (control && shift) {
                    saveAs();
                } else if (control) {
                    save();
                }
            }
            case YH_KEY_O -> {
                if (control) {
                    open();
                }
            }
            case YH_KEY_N -> {
                if (control) {
                    newSceneActivity();
                }
            }
        }

        return true;
    }

    public void open() {
        FileDialog.openFile(EditorLayer.OpenFileDialogId, "scene file(*.yaml){.yaml}", () -> {
            openActivity(ImGuiFileDialog.getFilePathName());
        });
    }

    public void save() {
        if (activeScene.getUri() != null) {
            saveActivity(activeScene.getUri());
        } else {
            saveAs();
        }
    }

    public void saveAs() {
        FileDialog.saveFile(EditorLayer.SaveFileDialogId, "scene file(*.yaml){.yaml}", () -> {
            saveActivity(ImGuiFileDialog.getFilePathName());
        });
    }

    public void newSceneActivity() {
        Scene scene = new Scene("Scene" + scenes.size(), null);
        scenes.add(scene);
        setActiveScene(scene);
    }

    public void openActivity(String file) {
        Scene scene = SceneSerializer.deserialize(file);
        scenes.add(scene);
        setActiveScene(scene);
    }


    public void saveActivity(String directory) {
        SceneSerializer.serialize(activeScene, directory);
    }

    private void setActiveScene(Scene scene) {
        activeScene = scene;
        sceneHierarchyPanel.setScene(scene);
    }
}
