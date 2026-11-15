import static org.lwjgl.glfw.GLFW.glfwGetTime;

import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Renderer.RendererStatistics;
import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Utils.OrthographicCameraController;
import GameEngine.Engine.Utils.TimeStep;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ExampleLayer2DBatchRenderer extends Layer {
    private final OrthographicCameraController cameraController;
    private BatchRenderer2D renderer;
    private double lastRenderTime, average, count;
    private Texture bricks;
    private Texture dirt;

    public ExampleLayer2DBatchRenderer() {
        super("Example Layer 2D");
        cameraController = new OrthographicCameraController(1280f / 720f);
    }

    @Override
    public void onAttach() {
        bricks = Texture.createFromResource("/textures/bricks.png");
        dirt = Texture.createFromResource("/textures/dirt.png");
        renderer = Application.get().getRenderer2D();
        lastRenderTime = glfwGetTime();
        average = 0;
        count = 0;
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        renderer.getStatistics().reset();
        cameraController.onUpdate(timeStep);

        RendererCommandAPI.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();
        renderer.begin(cameraController.getCamera());

        // renderer.drawQuad(
        //         new Vector2f(-0.6f, 0.6f), new Vector3f(.2f, 0.3f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
        // renderer.drawQuad(
        //         new Vector2f(-0.4f, -0.4f), new Vector3f(.3f, 0.3f, 1.0f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        // renderer.drawQuad(
        //         new Vector2f(0.4f, 0.2f), new Vector3f(.2f, 0.2f, 1.0f), new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));
        // renderer.drawQuad(new Vector2f(0.0f, 0.0f), new Vector3f(4.0f, 4.0f, 1.0f), bricks, 20.0f, -1);
        // renderer.drawRotatedQuad(
        //         new Vector2f(2.0f, 2.0f),
        //         new Vector3f(1.0f, 1.0f, 1.0f),
        //         new Vector3f(0.0f, 0.0f, (float) count),
        //         dirt,
        //         3.0f,
        //         -1);
        // renderer.drawQuad(new Vector2f(-1.0f, 2.0f), new Vector3f(1.0f, 1.0f, 1.0f), dirt, -1);
        double time = glfwGetTime();
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                renderer.drawQuad(
                        new Vector2f(-2.0f + (i * 0.05f), -2.0f + (j * 0.05f)),
                        new Vector3f(0.04f, 0.04f, 1.0f),
                        new Vector4f((float) i / 50, 0.4f, (float) j / 50, 0.5f));
            }
        }

        if (count <= 300) {
            average = new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds();
            count++;
        } else {
            average = average * (count - 1) / count
                    + (new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds()) / count;
            count++;
        }

        lastRenderTime = time;
        renderer.end();
    }

    @Override
    public void onImGuiRender() {
        super.onImGuiRender();
        RendererStatistics stats = renderer.getStatistics();
        ImGui.begin("Stats");
        ImGui.text("Renderer2D Stats:");
        ImGui.text("Draw Calls: " + stats.getDrawCallsCount());
        ImGui.text("Quads: " + stats.getQuadsCount());
        ImGui.text("Average Rendering time for one frame: " + average);
        ImGui.end();
    }

    @Override
    public void onDetach() {
        bricks.delete();
        dirt.delete();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }
}
