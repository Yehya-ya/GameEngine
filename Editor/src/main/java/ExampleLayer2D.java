import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Renderer.*;
import GameEngine.Engine.Utils.OrthographicCameraController;
import GameEngine.Engine.Utils.TimeStep;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_INFO;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class ExampleLayer2D extends Layer {
    private final OrthographicCameraController cameraController;
    double lastRenderTime;
    private Texture bricks;
    private Texture dirt;

    public ExampleLayer2D() {
        super("Example Layer 2D");
        cameraController = new OrthographicCameraController(1280f / 720f, true);
    }

    @Override
    public void onAttach() {
        bricks = Texture.create("assets/textures/bricks.png");
        dirt = Texture.create("assets/textures/dirt.png");
        Renderer2D.init();
        lastRenderTime = glfwGetTime();
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        RendererCommandAPI.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();

        RendererStatistics.getInstance().reset();
        cameraController.onUpdate(timeStep);
        Renderer2D.begin(cameraController.getCamera());

        Renderer2D.drawQuad(new Vector2f(0.0f, 0.0f), new Vector2f(4.0f, 4.0f), bricks, 20.0f);
        Renderer2D.drawRotatedQuad(new Vector2f(2.0f, 2.0f), new Vector2f(1.0f, 1.0f), 45, dirt, 3.0f);
        Renderer2D.drawQuad(new Vector2f(-1.0f, 2.0f), new Vector2f(1.0f, 1.0f), dirt);

        double time = glfwGetTime();
        for (int i = 0; i < 117; i++) {
            for (int j = 0; j < 117; j++) {
                Renderer2D.drawQuad(new Vector2f(-2.0f + (i * 0.01f), -2.0f + (j * 0.01f)), new Vector2f(0.009f, 0.009f), new Vector4f((float)i/114, 0.4f, (float)j/114, 0.5f));
            }
        }
        YH_LOG_INFO("time : {}", new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds());
        lastRenderTime = time;
        Renderer2D.end();
    }

    @Override
    public void onImgRender() {
        super.onImgRender();
        RendererStatistics stats = RendererStatistics.getInstance();
        ImGui.begin("Settings");
        ImGui.text("Renderer2D Stats:");
        ImGui.text("Draw Calls: " + stats.getDrawCallsCount());
        ImGui.text("Quads: " + stats.getQuadsCount());
        ImGui.end();
    }

    @Override
    public void onDetach() {
        Renderer2D.shutdown();
        bricks.delete();
        dirt.delete();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }
}
