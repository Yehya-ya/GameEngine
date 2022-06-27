import Engine.Core.Layer;
import Engine.Events.Event;
import Engine.Renderer.Renderer2D;
import Engine.Renderer.Texture;
import Engine.Utils.OrthographicCameraController;
import Engine.Utils.TimeStep;
import org.joml.Vector2f;
import org.joml.Vector4f;

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

        Renderer2D.drawQuad(new Vector2f(-0.6f, 0.6f), new Vector2f(.2f, 0.3f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        Renderer2D.drawQuad(new Vector2f(-0.4f, -0.4f), new Vector2f(.3f, 0.3f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        Renderer2D.drawQuad(new Vector2f(0.4f, 0.2f), new Vector2f(.2f, 0.2f), texture);

        Renderer2D.end();
    }

    @Override
    public void onDetach() {
        Renderer2D.shutdown();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }
}
