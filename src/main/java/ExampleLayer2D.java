import Engine.Core.Layer;
import Engine.Events.Event;
import Engine.Renderer.Renderer2D;
import Engine.Renderer.Texture;
import Engine.Utils.OrthographicCameraController;
import Engine.Utils.TimeStep;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static Engine.Utils.YH_Log.YH_LOG_INFO;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class ExampleLayer2D extends Layer {
    private final OrthographicCameraController cameraController;
    private Texture texture;

    public ExampleLayer2D() {
        super("Example Layer 2D");
        cameraController = new OrthographicCameraController(1280f / 720f, true);
    }

    @Override
    public void onAttach() {
        texture = Texture.create("assets/textures/bricks.png");
        Renderer2D.init();
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        cameraController.onUpdate(timeStep);

        Renderer2D.begin(cameraController.getCamera());
        double time = glfwGetTime();
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 200; j++) {
                Renderer2D.drawQuad(new Vector2f(-1.0f + (i * 0.01f), -1.0f + (j * 0.01f)), new Vector2f(0.003f, 0.003f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
            }
        }
        YH_LOG_INFO("time : {}", new TimeStep((float) (glfwGetTime() - time)).getMilliseconds());
        Renderer2D.end();
    }

    @Override
    public void onDetach() {
        Renderer2D.shutdown();
        texture.delete();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }
}
