import Engine.Layer;
import Engine.Renderer.Buffer.BufferLayout;
import Engine.Renderer.Buffer.IndexBuffer;
import Engine.Renderer.Buffer.VertexBuffer;
import Engine.Renderer.Camera.OrthographicCamera;
import Engine.Renderer.Renderer;
import Engine.Renderer.ShaderProgram;
import Engine.Renderer.VertexArray;
import Engine.Utils.TimeStep;
import imgui.ImGui;
import org.joml.Matrix4f;

public class ExampleLayer extends Layer {
    private final OrthographicCamera camera;
    private final VertexArray vertexArray;
    private final ShaderProgram shaderProgram;

    public ExampleLayer() {
        super("Example");
        camera = new OrthographicCamera(-1.6f, 1.6f, -0.9f, 0.9f);
        vertexArray = VertexArray.create();
        float[] vertices = {-0.5f, -0.5f, 0.5f, -0.5f, 0.0f, 0.5f};
        VertexBuffer vertexBuffer = VertexBuffer.create(vertices);
        BufferLayout bufferLayout = new BufferLayout()
                .addBufferElement(BufferLayout.ShaderDataType.Float2, "aPos", false);
        vertexBuffer.setLayout(bufferLayout);
        vertexArray.addVertexBuffer(vertexBuffer);

        int[] indices = {0, 1, 2};

        IndexBuffer indexBuffer = IndexBuffer.create(indices);
        vertexArray.setIndexBuffer(indexBuffer);

        shaderProgram = ShaderProgram.create("vertexShader", "fragmentShader");
        shaderProgram.bind();
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        Renderer.beginScene(camera);
        Renderer.submit(shaderProgram, vertexArray, new Matrix4f());
        Renderer.endScene();
    }
}
