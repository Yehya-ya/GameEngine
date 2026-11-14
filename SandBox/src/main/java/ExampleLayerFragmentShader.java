import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Core.Window;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Events.EventDispatcher;
import GameEngine.Engine.Events.EventType;
import GameEngine.Engine.Events.WindowEvents;
import GameEngine.Engine.Renderer.Buffer.BufferLayout;
import GameEngine.Engine.Renderer.Buffer.IndexBuffer;
import GameEngine.Engine.Renderer.Buffer.VertexBuffer;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Renderer.ShaderProgram;
import GameEngine.Engine.Renderer.VertexArray;
import GameEngine.Engine.Utils.TimeStep;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ExampleLayerFragmentShader extends Layer {
    public static final String Circles = "/shaders/fragment/circles.glsl";
    public static final String ColorCircle = "/shaders/fragment/colorCircle.glsl";
    private static final String vertexShader = "/shaders/vertex/vertexShader.glsl";
    private static final float[] screenCorners = {-1, -1, 1, -1, 1, 1, -1, 1};
    private static final int[] screenTriangles = {0, 1, 2, 2, 3, 0};

    private final String fragmentShader;
    private final Vector2f resolution;
    private final Vector2f firstCircleCenter = new Vector2f(0.35f, 0.5f);
    private final Vector2f secondCircleCenter = new Vector2f(0.65f, 0.5f);
    private float elapsedSeconds;
    private ShaderProgram shader;
    private VertexArray screen;

    public ExampleLayerFragmentShader(String fragmentShader) {
        super("Fragment Shader Example");
        this.fragmentShader = fragmentShader;
        Window window = Application.get().getWindow();
        resolution = new Vector2f(window.getWidth(), window.getHeight());
    }

    @Override
    public void onAttach() {
        shader = ShaderProgram.createFromResources("fragmentShaderExample", vertexShader, fragmentShader);

        VertexBuffer cornersBuffer = VertexBuffer.create(screenCorners);
        cornersBuffer.setLayout(new BufferLayout().addBufferElement(BufferLayout.ShaderDataType.Float2, "aPos", false));
        screen = VertexArray.create();
        screen.addVertexBuffer(cornersBuffer);
        screen.setIndexBuffer(IndexBuffer.create(screenTriangles));

        elapsedSeconds = 0;
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        elapsedSeconds += timeStep.getSeconds();

        RendererCommandAPI.setClearColor(new Vector4f(0, 0, 0, 1));
        RendererCommandAPI.clear();

        shader.bind();
        shader.UploadUniformFloat2("u_resolution", resolution);
        shader.UploadUniformFloat("u_time", elapsedSeconds);
        shader.UploadUniformFloat2("point1", firstCircleCenter);
        shader.UploadUniformFloat2("point2", secondCircleCenter);
        screen.bind();
        RendererCommandAPI.drawIndexed(screen);
    }

    @Override
    public void onEvent(Event event) {
        new EventDispatcher(event).dispatch(EventType.WindowResize, this::onWindowResize);
    }

    @Override
    public void onDetach() {
        screen.delete();
        shader.delete();
    }

    private boolean onWindowResize(Event event) {
        WindowEvents.WindowResizeEvent resizeEvent = (WindowEvents.WindowResizeEvent) event;
        resolution.set(resizeEvent.getWidth(), resizeEvent.getHeight());
        return false;
    }
}
