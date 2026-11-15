import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Core.Window;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Events.EventDispatcher;
import GameEngine.Engine.Events.EventType;
import GameEngine.Engine.Events.WindowEvents;
import GameEngine.Engine.Renderer.Camera.PerspectiveCamera;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Renderer.ShaderProgram;
import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Renderer.VertexArray;
import GameEngine.Engine.Utils.TimeStep;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ExampleLayer3D extends Layer {
    private static final float radiansPerSecond = 0.8f;
    private static final int textureSlot = 0;

    private final PerspectiveCamera camera;
    private final Matrix4f cubeTransformation = new Matrix4f();
    private float cubeAngle;
    private ShaderProgram shader;
    private Texture bricks;
    private VertexArray cube;

    public ExampleLayer3D(Window window) {
        super("3D Example");
        camera = new PerspectiveCamera((float) window.getWidth() / window.getHeight());
        camera.setPosition(new Vector3f(0, 0, 6));
    }

    @Override
    public void onAttach() {
        shader = ShaderProgram.createFromResources(
                "texturedModel", "/shaders/vertex/vertexShader3D.glsl", "/shaders/fragment/textureFragmentShader.glsl");
        bricks = Texture.createFromResource("/textures/bricks.png");
        cube = ObjModel.fromResource("/objects/cube.obj").createVertexArray();
        cubeAngle = 0;
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        cubeAngle += timeStep.getSeconds() * radiansPerSecond;
        cubeTransformation.identity().rotateXYZ(cubeAngle * 0.5f, cubeAngle, 0);

        RendererCommandAPI.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();

        bricks.bind(textureSlot);
        shader.bind();
        shader.uploadUniformMat4("projectionMatrix", camera.getProjectionMatrix());
        shader.uploadUniformMat4("viewMatrix", camera.getViewMatrix());
        shader.uploadUniformMat4("transformationMatrix", cubeTransformation);
        shader.uploadUniformInt("uTexture", textureSlot);
        cube.bind();
        RendererCommandAPI.drawIndexed(cube);
    }

    @Override
    public void onEvent(Event event) {
        new EventDispatcher(event).dispatch(EventType.WindowResize, this::onWindowResize);
    }

    @Override
    public void onDetach() {
        cube.delete();
        bricks.delete();
        shader.delete();
    }

    private boolean onWindowResize(Event event) {
        WindowEvents.WindowResizeEvent resizeEvent = (WindowEvents.WindowResizeEvent) event;
        boolean isMinimized = resizeEvent.getHeight() == 0;
        if (!isMinimized) {
            camera.setAspectRatio((float) resizeEvent.getWidth() / resizeEvent.getHeight());
        }
        return false;
    }
}
