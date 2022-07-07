import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.ECS.Components.CameraComponent;
import GameEngine.Engine.ECS.Components.SpriteComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Entity;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import GameEngine.Engine.Renderer.Buffer.FrameBuffer;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Renderer.RendererStatistics;
import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Utils.OrthographicCameraController;
import GameEngine.Engine.Utils.TimeStep;
import Panels.SceneHierarchyPanel;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class EditorLayer extends Layer {
    private final OrthographicCameraController cameraController;
    private double lastRenderTime, average, count;
    private Texture bricks;
    private Texture dirt;
    private FrameBuffer frameBuffer;
    private Vector2f viewportSize;
    private boolean isViewPortIsFocused, isViewPortIsHovered;
    private Scene scene;
    private Entity camera;
    private ImBoolean isCameraActive;
    private SceneHierarchyPanel sceneHierarchyPanel;

    public EditorLayer() {
        super("Example Layer 2D");
        cameraController = new OrthographicCameraController(1280f / 720f, 3.0f);
    }

    @Override
    public void onAttach() {
        bricks = Texture.create("assets/textures/bricks.png");
        dirt = Texture.create("assets/textures/dirt.png");
        BatchRenderer2D.init();
        lastRenderTime = glfwGetTime();
        average = 0;
        count = 0;

        FrameBuffer.FrameBufferSpecification specification = new FrameBuffer.FrameBufferSpecification();
        specification.width = 1280;
        specification.height = 720;
        frameBuffer = FrameBuffer.create(specification);
        viewportSize = new Vector2f(0);
        isViewPortIsFocused = false;
        isViewPortIsHovered = false;

        scene = new Scene();
        //////////////////////
        // BatchRenderer2D.drawQuad(new Vector2f(-0.6f, 0.6f), new Vector2f(.2f, 0.3f), );
        Entity square = scene.createEntity();
        TransformComponent transformComponent = square.getComponent(TransformComponent.class);
        transformComponent.transform = new Vector3f(-0.6f, 0.6f, 1.0f);
        transformComponent.size = new Vector2f(.2f, 0.3f);
        SpriteComponent spriteComponent = new SpriteComponent(new Vector4f(0.3f, 1.0f, 0.3f, 1.0f));
        square.addComponent(spriteComponent);

        //////////////////////
        // BatchRenderer2D.drawQuad(new Vector2f(-0.4f, -0.4f), new Vector2f(.3f, 0.3f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        Entity square2 = scene.createEntity();
        TransformComponent transformComponent2 = square2.getComponent(TransformComponent.class);
        transformComponent2.transform = new Vector3f(-0.4f, -0.4f, 1.0f);
        transformComponent2.size = new Vector2f(.3f, 0.3f);
        SpriteComponent spriteComponent2 = new SpriteComponent(new Vector4f(1.0f, 0.3f, 0.2f, 1.0f));
        square2.addComponent(spriteComponent2);

        //////////////////////
        // BatchRenderer2D.drawQuad(new Vector2f(0.0f, 0.0f), new Vector2f(4.0f, 4.0f), bricks, 20.0f);
        Entity square4 = scene.createEntity();
        TransformComponent transformComponent3 = square4.getComponent(TransformComponent.class);
        transformComponent3.transform = new Vector3f(-0.0f, -0.0f, 0.0f);
        transformComponent3.size = new Vector2f(4.0f, 4.0f);
        SpriteComponent spriteComponent4 = new SpriteComponent(bricks, 20.0f);
        square4.addComponent(spriteComponent4);

        ///////////////////////
        camera = scene.createEntity("Camera");
        CameraComponent cameraComponent = new CameraComponent(cameraController.getCamera(), true);
        camera.addComponent(cameraComponent);
        isCameraActive = new ImBoolean(true);

        sceneHierarchyPanel = new SceneHierarchyPanel(scene);
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        double time = glfwGetTime();
        // update
        if (isViewPortIsFocused) {
            cameraController.onUpdate(timeStep);
        }

        if (!viewportSize.equals(frameBuffer.getSpecification().width, frameBuffer.getSpecification().height)) {
            frameBuffer.resize((int) viewportSize.x, (int) viewportSize.y);
            cameraController.resize(viewportSize.x, viewportSize.y);
        }

        // render
        RendererStatistics.getInstance().reset();
        frameBuffer.bind();
        RendererCommandAPI.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();

        // scene
        scene.onUpdate(timeStep);

        frameBuffer.unbind();

        if (count <= 300) {
            average = new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds();
        } else {
            average = average * (30 - 1) / 30 + (new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds()) / 30;
        }
        count++;

        lastRenderTime = time;
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
                // Disabling fullscreen would allow the window to be moved to the front of other windows,
                // which we can't undo at the moment without finer window depth/z control.
                //ImGui.MenuItem("Fullscreen", NULL, &opt_fullscreen_persistant);

                if (ImGui.menuItem("Exit")) Application.get().close();
                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        sceneHierarchyPanel.onImGuiRender();

        ImGui.begin("Settings");
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
        cameraController.onEvent(event);
    }
}
