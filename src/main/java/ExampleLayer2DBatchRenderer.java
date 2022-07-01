import Engine.Core.Layer;
import Engine.Events.Event;
import Engine.Renderer.BatchRenderer2D;
import Engine.Renderer.Texture;
import Engine.Utils.OrthographicCameraController;
import Engine.Utils.TimeStep;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static Engine.Utils.YH_Log.YH_LOG_INFO;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class ExampleLayer2DBatchRenderer extends Layer {
    private final OrthographicCameraController cameraController;
    double lastRenderTime;
    private Texture bricks;
    private Texture dirt;

    public ExampleLayer2DBatchRenderer() {
        super("Example Layer 2D");
        cameraController = new OrthographicCameraController(1280f / 720f, true);
    }

    @Override
    public void onAttach() {
        bricks = Texture.create("assets/textures/bricks.png");
        dirt = Texture.create("assets/textures/dirt.png");
        BatchRenderer2D.init();
        lastRenderTime = glfwGetTime();
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        cameraController.onUpdate(timeStep);

        BatchRenderer2D.begin(cameraController.getCamera());

        // BatchRenderer2D.drawQuad(new Vector2f(-0.6f, 0.6f), new Vector2f(.2f, 0.3f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
        // BatchRenderer2D.drawQuad(new Vector2f(-0.4f, -0.4f), new Vector2f(.3f, 0.3f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        // BatchRenderer2D.drawQuad(new Vector2f(0.4f, 0.2f), new Vector2f(.2f, 0.2f), new Vector4f(0.0f,0.0f,1.0f,1.0f));
        BatchRenderer2D.drawQuad(new Vector2f(-2.0f, -2.0f), new Vector2f(4.0f, 4.0f), bricks, 20.0f);
        BatchRenderer2D.drawQuad(new Vector2f(2.0f, 2.0f), new Vector2f(1.0f, 1.0f), dirt, 3.0f);
        BatchRenderer2D.drawQuad(new Vector2f(-1.0f, 2.0f), new Vector2f(1.0f, 1.0f), dirt, 1.0f);

        double time = glfwGetTime();
        for (int i = 0; i < 300; i++) {
            for (int j = 0; j < 300; j++) {
                BatchRenderer2D.drawQuad(new Vector2f(-1.0f + (i * 0.01f), -1.0f + (j * 0.01f)), new Vector2f(0.009f, 0.009f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
            }
        }
        YH_LOG_INFO("time : {}", new TimeStep((float) (glfwGetTime() - lastRenderTime)).getMilliseconds());
        lastRenderTime = time;
        BatchRenderer2D.end();
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
