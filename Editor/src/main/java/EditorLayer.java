import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import GameEngine.Engine.Renderer.Buffer.FrameBuffer;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Renderer.RendererStatistics;
import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Utils.OrthographicCameraController;
import GameEngine.Engine.Utils.TimeStep;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class EditorLayer extends Layer {
    private final OrthographicCameraController cameraController;
    private double lastRenderTime, average, count;
    private Texture bricks;
    private Texture dirt;
    private FrameBuffer frameBuffer;

    public EditorLayer() {
        super("Example Layer 2D");
        cameraController = new OrthographicCameraController(1280f / 720f, true);
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
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        RendererStatistics.getInstance().reset();
        cameraController.onUpdate(timeStep);

        frameBuffer.bind();
        RendererCommandAPI.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();
        BatchRenderer2D.begin(cameraController.getCamera());

        // BatchRenderer2D.drawQuad(new Vector2f(-0.6f, 0.6f), new Vector2f(.2f, 0.3f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
        // BatchRenderer2D.drawQuad(new Vector2f(-0.4f, -0.4f), new Vector2f(.3f, 0.3f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        // BatchRenderer2D.drawQuad(new Vector2f(0.4f, 0.2f), new Vector2f(.2f, 0.2f), new Vector4f(0.0f,0.0f,1.0f,1.0f));
        BatchRenderer2D.drawQuad(new Vector2f(0.0f, 0.0f), new Vector2f(4.0f, 4.0f), bricks, 20.0f);
        BatchRenderer2D.drawRotatedQuad(new Vector2f(2.0f, 2.0f), new Vector2f(1.0f, 1.0f), (float) (count), dirt, 3.0f);
        BatchRenderer2D.drawQuad(new Vector2f(-1.0f, 2.0f), new Vector2f(1.0f, 1.0f), dirt);
        double time = glfwGetTime();
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                BatchRenderer2D.drawQuad(new Vector2f(-2.0f + (i * 0.05f), -2.0f + (j * 0.05f)), new Vector2f(0.04f, 0.04f), new Vector4f((float) i / 50, 0.4f, (float) j / 50, 0.5f));
            }
        }

        if (count <= 300) {
            average = new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds();
        } else {
            average = average * (count - 1) / count + (new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds()) / count;
        }
        count++;

        lastRenderTime = time;
        BatchRenderer2D.end();
        frameBuffer.unbind();
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

        ImGui.begin("Settings");
        RendererStatistics stats = RendererStatistics.getInstance();
        ImGui.text("Renderer2D Stats:");
        ImGui.text("Draw Calls: " + stats.getDrawCallsCount());
        ImGui.text("Quads: " + stats.getQuadsCount());
        ImGui.text("Average Rendering time for one frame: " + average);
        int textureID = frameBuffer.getColorAttachmentRendererId();
        ImGui.image(textureID, 1280.0f, 720.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        ImGui.end();

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
