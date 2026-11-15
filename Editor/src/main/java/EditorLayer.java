import static GameEngine.Engine.Utils.KeyCodes.*;

import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Input;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Core.Project;
import GameEngine.Engine.ECS.Entity;
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
import Panels.Selection;
import Utils.FileDialog;
import Utils.FileDialog.FileTypes;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImBoolean;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;

public class EditorLayer extends Layer {
    public static final String ViewportWindowName = "Viewport";
    public static final String StatsWindowName = "Stats";
    public static final String ToolbarWindowName = "##toolbar";
    private static final FileTypes sceneFileTypes = new FileTypes("Scene files", List.of(".yaml"));
    private static final String untitledSceneFile = "untitled.yaml";
    private final Project project;
    private double average;
    private Vector2f viewportSize;
    private int sceneCount;
    private Scene editorScene;
    private Scene runtimeScene;
    private Scene activeScene;
    private List<Scene> scenesToDeleteNextFrame;
    private BatchRenderer2D renderer;
    private FileDialog fileDialog;
    private Selection selection;
    private Viewport viewport;
    private SceneHierarchyPanel sceneHierarchyPanel;
    private ContentBrowserPanel contentBrowserPanel;
    private final Texture iconPlay;
    private final Texture iconStop;
    private final ImGuiWindowClass toolbarWindowClass;
    private SceneState sceneState;
    private boolean isLayoutResetRequested;

    public EditorLayer(Project project) {
        super("Example Layer 2D");

        this.project = project;
        iconPlay = Texture.createFromResource("/Icons/PlayButton.png");
        iconStop = Texture.createFromResource("/Icons/StopButton.png");
        toolbarWindowClass = new ImGuiWindowClass();
        toolbarWindowClass.setDockNodeFlagsOverrideSet(DockLayout.ToolbarNodeFlags);
    }

    @Override
    public void delete() {
        iconPlay.delete();
        iconStop.delete();
        super.delete();
    }

    @Override
    public void onAttach() {
        sceneCount = 0;
        scenesToDeleteNextFrame = new ArrayList<>();
        renderer = Application.get().getRenderer2D();
        average = 0;

        viewportSize = new Vector2f(0);

        fileDialog = new FileDialog(project.getAssetDirectory());
        selection = new Selection();
        viewport = new Viewport(selection, renderer);
        sceneHierarchyPanel = new SceneHierarchyPanel(selection, fileDialog);
        contentBrowserPanel = new ContentBrowserPanel(project.getAssetDirectory());
        sceneState = SceneState.Edit;
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        // update
        if (activeScene != null) {
            viewport.resize(viewportSize);
        }

        // render
        renderer.getStatistics().reset();

        // scene
        if (activeScene != null) {
            viewport.onUpdate(timeStep);
        }

        average = average * (30 - 1) / 30 + timeStep.getMilliseconds() / 30;
    }

    public void onScenePlay() {
        boolean canPlay = editorScene != null && sceneState == SceneState.Edit;
        if (!canPlay) {
            return;
        }
        sceneState = SceneState.Play;
        runtimeScene = editorScene.copy();
        showScene(runtimeScene);
        viewport.play(runtimeScene);
    }

    public void onSceneStop() {
        if (sceneState != SceneState.Play) {
            return;
        }
        sceneState = SceneState.Edit;
        scenesToDeleteNextFrame.add(runtimeScene);
        runtimeScene = null;
        showScene(editorScene);
        viewport.edit(editorScene);
    }

    @Override
    public void onImgRender() {
        super.onImgRender();
        deleteScenesFromPreviousFrame();

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
            window_flags |= ImGuiWindowFlags.NoTitleBar
                    | ImGuiWindowFlags.NoCollapse
                    | ImGuiWindowFlags.NoResize
                    | ImGuiWindowFlags.NoMove;
            window_flags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
        }

        // When using ImGuiDockNodeFlags_PassthruCentralNode, DockSpace() will render our background and handle the
        // pass-thru hole, so we ask Begin() to not render a background.
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
            if (isLayoutResetRequested || !DockLayout.exists(dockspace_id)) {
                DockLayout.applyDefault(dockspace_id, ImGui.getContentRegionAvail());
                isLayoutResetRequested = false;
            }
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
            if (ImGui.beginMenu("Window")) {
                if (ImGui.menuItem("Reset Layout")) {
                    isLayoutResetRequested = true;
                }
                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        sceneHierarchyPanel.onImGuiRender();
        contentBrowserPanel.onImGuiRender();

        ImGui.begin(StatsWindowName);
        RendererStatistics stats = renderer.getStatistics();
        ImGui.text("Renderer2D Stats:");
        ImGui.text("Draw Calls: " + stats.getDrawCallsCount());
        ImGui.text("Quads: " + stats.getQuadsCount());
        ImGui.textWrapped("Average Rendering time for one frame: " + String.format("%2f", average) + " ms");
        ImGui.end();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin(ViewportWindowName);
        boolean isViewportHovered = ImGui.isWindowHovered();
        Application.get().getImGuiLayer().setBlockingEvents(!isViewportHovered);
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
            viewport.onImGuiRender();
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
        deleteScenesFromPreviousFrame();
        if (runtimeScene != null) {
            runtimeScene.delete();
            runtimeScene = null;
        }
        if (editorScene != null) {
            editorScene.delete();
            editorScene = null;
        }
        activeScene = null;
        viewport.delete();
        contentBrowserPanel.delete();
    }

    @Override
    public void onEvent(Event event) {
        (new EventDispatcher(event)).dispatch(EventType.KeyPressed, this::onKeyPressed);
        if (activeScene != null) {
            viewport.onEvent(event);
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
            case YH_KEY_D -> {
                if (control) {
                    duplicateSelectedEntity();
                }
            }
        }

        return true;
    }

    public void open() {
        fileDialog.openFile(sceneFileTypes).ifPresent(file -> openActivity(file.toString()));
    }

    public void save() {
        if (editorScene != null) {
            if (editorScene.getUri() != null) {
                saveActivity(editorScene.getUri());
            } else {
                saveAs();
            }
        }
    }

    public void saveAs() {
        if (editorScene != null) {
            fileDialog.saveFile(sceneFileTypes, untitledSceneFile).ifPresent(file -> saveActivity(file.toString()));
        }
    }

    public void newSceneActivity() {
        Scene scene = new Scene("Scene" + sceneCount++, null);
        setEditorScene(scene);
    }

    public void openActivity(String file) {
        Scene scene = SceneSerializer.deserialize(file, project);
        if (scene != null) {
            setEditorScene(scene);
        }
    }

    public void saveActivity(String directory) {
        SceneSerializer.serialize(editorScene, directory, project);
    }

    private void setEditorScene(Scene scene) {
        if (sceneState == SceneState.Play) {
            onSceneStop();
        }
        if (editorScene != null && editorScene != scene) {
            scenesToDeleteNextFrame.add(editorScene);
        }
        editorScene = scene;
        showScene(scene);
        viewport.resetCamera();
        viewport.edit(scene);
    }

    private void showScene(Scene scene) {
        activeScene = scene;
        selection.clear();
        sceneHierarchyPanel.setScene(scene);
    }

    private void duplicateSelectedEntity() {
        Entity selectedEntity = selection.getEntity();
        if (activeScene != null && selectedEntity != null) {
            activeScene.duplicateEntity(selectedEntity);
        }
    }

    private void deleteScenesFromPreviousFrame() {
        for (Scene scene : scenesToDeleteNextFrame) {
            scene.delete();
        }
        scenesToDeleteNextFrame.clear();
    }

    // UI Panels
    public void uiToolbar() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 2);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImVec4[] colors = ImGui.getStyle().getColors();
        ImVec4 buttonHovered = colors[ImGuiCol.ButtonHovered];
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonHovered.x, buttonHovered.y, buttonHovered.z, 0.5f);
        ImVec4 buttonActive = colors[ImGuiCol.ButtonActive];
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonActive.x, buttonActive.y, buttonActive.z, 0.5f);

        ImGui.setNextWindowClass(toolbarWindowClass);
        ImGui.begin(
                ToolbarWindowName,
                new ImBoolean(false),
                ImGuiWindowFlags.NoDecoration
                        | ImGuiWindowFlags.NoScrollbar
                        | ImGuiWindowFlags.NoScrollWithMouse
                        | ImGuiWindowFlags.NoNav);

        float size = ImGui.getWindowHeight() - 4.0f;
        Texture icon = sceneState == SceneState.Edit ? iconPlay : iconStop;
        ImGui.setCursorPosX((ImGui.getWindowContentRegionMaxX() * 0.5f) - (size * 0.5f));
        if (ImGui.imageButton("##SceneState", icon.getRendererId(), size, size, 0, 0, 1, 1)) {
            if (sceneState == SceneState.Edit) onScenePlay();
            else if (sceneState == SceneState.Play) onSceneStop();
        }
        ImGui.popStyleVar(3);
        ImGui.popStyleColor(3);
        ImGui.end();
    }

    public enum SceneState {
        Edit,
        Play
    }
}
