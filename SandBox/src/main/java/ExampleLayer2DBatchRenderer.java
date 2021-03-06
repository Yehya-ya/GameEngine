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
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class ExampleLayer2DBatchRenderer extends Layer {
    private final OrthographicCameraController cameraController;
    private double lastRenderTime, average, count;
    private Texture bricks;
    private Texture dirt;

    public ExampleLayer2DBatchRenderer() {
        super("Example Layer 2D");
        cameraController = new OrthographicCameraController(1280f / 720f);
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
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        RendererStatistics.getInstance().reset();
        cameraController.onUpdate(timeStep);

        RendererCommandAPI.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();
        BatchRenderer2D.begin(cameraController.getCamera());

        // BatchRenderer2D.drawQuad(new Vector2f(-0.6f, 0.6f), new Vector2f(.2f, 0.3f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
        // BatchRenderer2D.drawQuad(new Vector2f(-0.4f, -0.4f), new Vector2f(.3f, 0.3f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        // BatchRenderer2D.drawQuad(new Vector2f(0.4f, 0.2f), new Vector2f(.2f, 0.2f), new Vector4f(0.0f,0.0f,1.0f,1.0f));
        // BatchRenderer2D.drawQuad(new Vector2f(0.0f, 0.0f), new Vector2f(4.0f, 4.0f), bricks, 20.0f);
        // BatchRenderer2D.drawRotatedQuad(new Vector2f(2.0f, 2.0f), new Vector2f(1.0f, 1.0f), (float) (count), dirt, 3.0f);
        // BatchRenderer2D.drawQuad(new Vector2f(-1.0f, 2.0f), new Vector2f(1.0f, 1.0f), dirt);
        double time = glfwGetTime();
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                BatchRenderer2D.drawQuad(new Vector2f(-2.0f + (i * 0.05f), -2.0f + (j * 0.05f)), new Vector3f(0.04f, 0.04f, 1.0f), new Vector4f((float) i / 50, 0.4f, (float) j / 50, 0.5f));
            }
        }

        if (count <= 300) {
            average = new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds();
            count++;
        } else {
            average = average * (count - 1) / count + (new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds()) / count;
            count++;
        }

        lastRenderTime = time;
        BatchRenderer2D.end();
    }

    @Override
    public void onImgRender() {
        super.onImgRender();
        RendererStatistics stats = RendererStatistics.getInstance();
        ImGui.begin("Stats");
        ImGui.text("Renderer2D Stats:");
        ImGui.text("Draw Calls: " + stats.getDrawCallsCount());
        ImGui.text("Quads: " + stats.getQuadsCount());
        ImGui.text("Average Rendering time for one frame: " + average);
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
