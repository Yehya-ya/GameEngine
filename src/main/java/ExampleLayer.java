import Engine.Core.Layer;
import Engine.Events.Event;
import Engine.Renderer.Buffer.BufferLayout;
import Engine.Renderer.Buffer.IndexBuffer;
import Engine.Renderer.Buffer.VertexBuffer;
import Engine.Renderer.*;
import Engine.Utils.OrthographicCameraController;
import Engine.Utils.TimeStep;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ExampleLayer extends Layer {
    private final OrthographicCameraController cameraController;
    private final VertexArray triangle;
    private final VertexArray rectangle;
    private final ShaderLibrary shaderLibrary;
    private final ShaderProgram soldColorShader;
    private final ShaderProgram textureShader;

    private final Texture texture;
    private final float[] col;
    private final Matrix4f transformation;

    public ExampleLayer() {
        super("Example");
        col = new float[4];
        col[3] = 1.0f;
        transformation = new Matrix4f().translate(new Vector3f(0.2f, 0.2f, 0.0f)).scale(0.1f);
        cameraController = new OrthographicCameraController(1280f / 720f, true);
        shaderLibrary = new ShaderLibrary();

        triangle = VertexArray.create();
        float[] vertices = {-0.5f, -0.5f, 0.5f, -0.5f, 0.0f, 0.5f};
        VertexBuffer vertexBuffer = VertexBuffer.create(vertices);
        BufferLayout bufferLayout = new BufferLayout().addBufferElement(BufferLayout.ShaderDataType.Float2, "aPos", false);
        vertexBuffer.setLayout(bufferLayout);
        triangle.addVertexBuffer(vertexBuffer);

        int[] indices = {0, 1, 2};
        IndexBuffer indexBuffer = IndexBuffer.create(indices);
        triangle.setIndexBuffer(indexBuffer);

        rectangle = VertexArray.create();
        float[] rectangleVertices = {1.f, 1.f, 1.f, 1.f, -1.f, 1.f, .0f, 1.f, -1.f, -1.f, .0f, .0f, 1.f, -1.f, 1.f, .0f,};
        VertexBuffer rectangleVB = VertexBuffer.create(rectangleVertices);
        BufferLayout rectangleBL = new BufferLayout().addBufferElement(BufferLayout.ShaderDataType.Float2, "aPos", false).addBufferElement(BufferLayout.ShaderDataType.Float2, "aTexCoord", false);
        rectangleVB.setLayout(rectangleBL);
        rectangle.addVertexBuffer(rectangleVB);


        int[] rectangleIndices = {0, 1, 2, 0, 2, 3,};
        IndexBuffer rectangleIB = IndexBuffer.create(rectangleIndices);
        rectangle.setIndexBuffer(rectangleIB);

        // shaderProgram = ShaderProgram.create("vertex/vertexShader.glsl", "fragment/fragmentShader.glsl");
        soldColorShader = shaderLibrary.load("assets/shaders/shader.glsl");
        textureShader = shaderLibrary.load("assets/shaders/textureShader.glsl");

        texture = Texture.create("assets/textures/bricks.png");
        texture.bind();
    }

    @Override
    public void delete() {
        texture.delete();
        shaderLibrary.remove(soldColorShader);
        shaderLibrary.remove(textureShader);
        this.triangle.delete();
        this.rectangle.delete();
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        cameraController.onUpdate(timeStep);
        Renderer.beginScene(cameraController.getCamera());

        soldColorShader.bind();
        soldColorShader.UploadUniformFloat4("u_color", new Vector4f(col));
        Renderer.submit(soldColorShader, triangle, new Matrix4f());

        Renderer.submit(textureShader, rectangle, transformation);
        Renderer.endScene();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }

    @Override
    public void onImgRender() {
        ImGui.colorPicker4("color: ", col);
    }
}
