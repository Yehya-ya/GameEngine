import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Input;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.ECS.SceneSerializer;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Events.EventDispatcher;
import GameEngine.Engine.Events.EventType;
import GameEngine.Engine.Events.KeyEvent;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import GameEngine.Engine.Renderer.RendererStatistics;
import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Utils.TimeStep;
import Panels.ContentBrowserPanel;
import Panels.SceneHierarchyPanel;
import Utils.FileDialog;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.flag.*;
import imgui.type.ImBoolean;
import org.joml.Vector2f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static GameEngine.Engine.Utils.KeyCodes.*;

public class EditorLayer extends Layer {
    public static final long OpenFileDialogId = 101;
    public static final long SaveFileDialogId = 102;
    private double average;
    private Texture bricks;
    private Texture dirt;
    private Vector2f viewportSize;
    private List<Scene> scenes;
    private Scene activeScene;
    private SceneHierarchyPanel sceneHierarchyPanel;
    private ContentBrowserPanel contentBrowserPanel;
    private final Texture iconPlay;
    private final Texture iconStop;
    private SceneState sceneState;

    public EditorLayer() {
        super("Example Layer 2D");

        iconPlay = Texture.create("resources/Icons/PlayButton.png");
        iconStop = Texture.create("resources/Icons/StopButton.png");
    }

    @Override
    public void onAttach() {
        scenes = new ArrayList<>();
        bricks = Texture.create("assets/textures/bricks.png");
        dirt = Texture.create("assets/textures/dirt.png");
        BatchRenderer2D.init();
        average = 0;

        viewportSize = new Vector2f(0);

        sceneHierarchyPanel = new SceneHierarchyPanel();
        contentBrowserPanel = new ContentBrowserPanel();
        sceneState = SceneState.Edit;
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        // update
        if (activeScene != null) {
            activeScene.setViewportSize(viewportSize);
        }

        // render
        RendererStatistics.getInstance().reset();

        // scene
        if (activeScene != null) {
            activeScene.onUpdate(timeStep);
        }

        average = average * (30 - 1) / 30 + timeStep.getMilliseconds() / 30;
    }

    public void onScenePlay() {
        sceneState = SceneState.Play;
    }

    public void onSceneStop() {
        sceneState = SceneState.Edit;
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
        contentBrowserPanel.onImGuiRender();

        ImGui.begin("Stats");
        RendererStatistics stats = RendererStatistics.getInstance();
        ImGui.text("Renderer2D Stats:");
        ImGui.text("Draw Calls: " + stats.getDrawCallsCount());
        ImGui.text("Quads: " + stats.getQuadsCount());
        ImGui.textWrapped("Average Rendering time for one frame: " + String.format("%2f", average) + " ms");
        ImGui.end();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Viewport");
//        ImGui.beginTabBar("Tabs");
//        for (Scene scene : scenes) {
//            int tabFlags = ImGuiTabItemFlags.Trailing;
//
//            if (ImGui.beginTabItem(scene.getTitle(), tabFlags)) {
//                if (!scene.equals(activeScene)) {
//                    setActiveScene(scene);
//                }
//                ImGui.endTabItem();
//            }
//        }
//        ImGui.endTabBar();

        ImVec2 size = ImGui.getContentRegionAvail();
        viewportSize = new Vector2f(size.x, size.y);

        if (activeScene != null) {
            uiToolbar();
            activeScene.onImgRender();
        }

        if (ImGui.beginDragDropTarget()) {
            File file = ImGui.acceptDragDropPayload("CONTENT_BROWSER_ITEM");
            if (file != null) {
                int index = file.getName().lastIndexOf('.');
                String extension = index > 0 ? file.getName().substring(index + 1) : "";
                if (extension.equals("yaml")) {
                    openActivity(file.getPath());
                }
            }
            ImGui.endDragDropTarget();
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
        if (activeScene != null) {
            activeScene.onEvent(event);
        }
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
        if (activeScene != null) {
            if (activeScene.getUri() != null) {
                saveActivity(activeScene.getUri());
            } else {
                saveAs();
            }
        }
    }

    public void saveAs() {
        if (activeScene != null) {
            FileDialog.saveFile(EditorLayer.SaveFileDialogId, "scene file(*.yaml){.yaml}", () -> {
                saveActivity(ImGuiFileDialog.getFilePathName());
            });
        }
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

    // UI Panels
    public void uiToolbar() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 2);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        float[][] colors = ImGui.getStyle().getColors();
        float[] buttonHovered = colors[ImGuiCol.ButtonHovered];
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonHovered[0], buttonHovered[1], buttonHovered[2], 0.5f);
        float[] buttonActive = colors[ImGuiCol.ButtonActive];
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonActive[0], buttonActive[1], buttonActive[2], 0.5f);

        ImGui.begin("##toolbar", new ImBoolean(false), ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoNav);

        float size = ImGui.getWindowHeight() - 4.0f;
        Texture icon = sceneState == SceneState.Edit ? iconPlay : iconStop;
        ImGui.setCursorPosX((ImGui.getWindowContentRegionMaxX() * 0.5f) - (size * 0.5f));
        if (ImGui.imageButton(icon.getRendererId(), size, size, 0, 0, 1, 1, 0)) {
            if (sceneState == SceneState.Edit) onScenePlay();
            else if (sceneState == SceneState.Play) onSceneStop();
        }
        ImGui.popStyleVar(2);
        ImGui.popStyleColor(3);
        ImGui.end();
    }

    public enum SceneState {
        Edit, Play
    }
}
