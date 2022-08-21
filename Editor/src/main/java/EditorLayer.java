import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.ECS.SceneSerializer;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import GameEngine.Engine.Renderer.Buffer.FrameBuffer;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Renderer.RendererStatistics;
import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Utils.TimeStep;
import Panels.SceneHierarchyPanel;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class EditorLayer extends Layer {
    static final long OpenFileDialogId = 101;
    static final long SaveFileDialogId = 102;
    private double average;
    private Texture bricks;
    private Texture dirt;
    private FrameBuffer frameBuffer;
    private Vector2f viewportSize;
    private boolean isViewPortIsFocused, isViewPortIsHovered;
    private Scene activeScene;
    private SceneHierarchyPanel sceneHierarchyPanel;

    public EditorLayer() {
        super("Example Layer 2D");
    }

    @Override
    public void onAttach() {
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
                if (ImGui.menuItem("Open")) {
                    ImGuiFileDialog.openModal(
                            "openDialogKey",
                            "Choose a file",
                            "scene file(*.yaml){.yaml}",
                            "",
                            null,
                            250,
                            1,
                            EditorLayer.OpenFileDialogId,
                            ImGuiFileDialogFlags.HideColumnType | ImGuiFileDialogFlags.DisableCreateDirectoryButton);
                }
                if (ImGui.menuItem("Save as")) {
                    ImGuiFileDialog.openModal(
                            "openDialogKey",
                            "save as",
                            "scene file(*.yaml){.yaml}",
                            "untitled.yaml",
                            null,
                            250,
                            1,
                            EditorLayer.SaveFileDialogId,
                            ImGuiFileDialogFlags.HideColumnType | ImGuiFileDialogFlags.DisableCreateDirectoryButton | ImGuiFileDialogFlags.ConfirmOverwrite);
                }
                if (ImGui.menuItem("Exit")) {
                    Application.get().close();
                }
                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        if (ImGuiFileDialog.display("openDialogKey", ImGuiWindowFlags.NoCollapse, 500, 400, 1000, 800)) {
            if (ImGuiFileDialog.isOk()) {
                long userDatas = ImGuiFileDialog.getUserDatas();
                if (userDatas == OpenFileDialogId) {
                    open(ImGuiFileDialog.getFilePathName());
                } else if (userDatas == SaveFileDialogId) {
                    saveAs(ImGuiFileDialog.getFilePathName());
                }
            }
            ImGuiFileDialog.close();
        }
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

        isViewPortIsFocused = ImGui.isWindowFocused();
        isViewPortIsHovered = ImGui.isWindowHovered();
        Application.get().getImGuiLayer().setBlockingEvents(!isViewPortIsFocused || !isViewPortIsHovered);

        int textureID = frameBuffer.getColorAttachmentRendererId();
        ImVec2 size = ImGui.getContentRegionAvail();
        viewportSize = new Vector2f(size.x, size.y);

        ImGui.image(textureID, size.x, size.y, 0.0f, 1.0f, 1.0f, 0.0f);
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
    }

    public void open(String file) {
        activeScene = SceneSerializer.deserialize(file);
        sceneHierarchyPanel.setScene(activeScene);
    }


    public void saveAs(String directory) {
        SceneSerializer.serialize(activeScene, directory);
    }
}
