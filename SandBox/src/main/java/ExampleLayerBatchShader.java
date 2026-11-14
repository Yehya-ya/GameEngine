import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Renderer.Buffer.BufferLayout;
import GameEngine.Engine.Renderer.Buffer.IndexBuffer;
import GameEngine.Engine.Renderer.Buffer.VertexBuffer;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Renderer.ShaderProgram;
import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Renderer.VertexArray;
import GameEngine.Engine.Utils.OrthographicCameraController;
import GameEngine.Engine.Utils.TimeStep;
import java.util.List;
import java.util.stream.IntStream;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ExampleLayerBatchShader extends Layer {
    private static final int whiteSlot = 0;
    private static final int bricksSlot = 1;
    private static final int dirtSlot = 2;
    private static final int[] textureSlots = IntStream.range(0, 32).toArray();
    private static final Vector4f noTint = new Vector4f(1);
    private static final Vector2f quadSize = new Vector2f(0.4f);
    private static final List<Quad> quads = List.of(
            new Quad(new Vector2f(-0.25f, 0.23f), new Vector4f(0.8f, 0.2f, 0.3f, 1), whiteSlot, 1),
            new Quad(new Vector2f(0.25f, 0.23f), noTint, bricksSlot, 1),
            new Quad(new Vector2f(-0.25f, -0.23f), noTint, dirtSlot, 1),
            new Quad(new Vector2f(0.25f, -0.23f), new Vector4f(0.6f, 0.8f, 1, 1), bricksSlot, 4));
    private static final Vector2f[] cornerOffsets = {
        new Vector2f(-0.5f, -0.5f), new Vector2f(0.5f, -0.5f), new Vector2f(0.5f, 0.5f), new Vector2f(-0.5f, 0.5f)
    };
    private static final Vector2f[] cornerTextureCoordinates = {
        new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1), new Vector2f(0, 1)
    };
    private static final int[] quadCornerOrder = {0, 1, 2, 2, 3, 0};
    private static final int floatsPerVertex = 11;

    private final OrthographicCameraController cameraController;
    private ShaderProgram shader;
    private Texture white;
    private Texture bricks;
    private Texture dirt;
    private VertexArray quadsArray;

    public ExampleLayerBatchShader() {
        super("Batch Shader Example");
        cameraController = new OrthographicCameraController(1280f / 720f);
    }

    @Override
    public void onAttach() {
        shader = ShaderProgram.createFromResource("/shaders/shader2DBatchRenderer.glsl");
        white = Texture.createWhite();
        bricks = Texture.createFromResource("/textures/bricks.png");
        dirt = Texture.createFromResource("/textures/dirt.png");

        VertexBuffer vertexBuffer = VertexBuffer.create(quadVertices());
        vertexBuffer.setLayout(new BufferLayout()
                .addBufferElement(BufferLayout.ShaderDataType.Float3, "aPos", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float4, "aColor", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float2, "aTexCoord", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float, "aTilingFactor", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float, "aTextureIndex", false));
        quadsArray = VertexArray.create();
        quadsArray.addVertexBuffer(vertexBuffer);
        quadsArray.setIndexBuffer(IndexBuffer.create(quadIndices()));
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        cameraController.onUpdate(timeStep);

        RendererCommandAPI.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();

        white.bind(whiteSlot);
        bricks.bind(bricksSlot);
        dirt.bind(dirtSlot);
        shader.bind();
        shader.UploadUniformMat4("uViewProjection", cameraController.getCamera().getViewProjectionMatrix());
        shader.UploadUniformIntArray("uTextures", textureSlots);
        quadsArray.bind();
        RendererCommandAPI.drawIndexed(quadsArray);
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }

    @Override
    public void onDetach() {
        quadsArray.delete();
        dirt.delete();
        bricks.delete();
        white.delete();
        shader.delete();
    }

    private static float[] quadVertices() {
        float[] vertices = new float[quads.size() * cornerOffsets.length * floatsPerVertex];
        int next = 0;
        for (Quad quad : quads) {
            for (int corner = 0; corner < cornerOffsets.length; corner++) {
                vertices[next++] = quad.center.x + cornerOffsets[corner].x * quadSize.x;
                vertices[next++] = quad.center.y + cornerOffsets[corner].y * quadSize.y;
                vertices[next++] = 0;
                vertices[next++] = quad.color.x;
                vertices[next++] = quad.color.y;
                vertices[next++] = quad.color.z;
                vertices[next++] = quad.color.w;
                vertices[next++] = cornerTextureCoordinates[corner].x;
                vertices[next++] = cornerTextureCoordinates[corner].y;
                vertices[next++] = quad.tilingFactor;
                vertices[next++] = quad.textureSlot;
            }
        }
        return vertices;
    }

    private static int[] quadIndices() {
        int[] indices = new int[quads.size() * quadCornerOrder.length];
        for (int quad = 0; quad < quads.size(); quad++) {
            for (int i = 0; i < quadCornerOrder.length; i++) {
                indices[quad * quadCornerOrder.length + i] = quad * cornerOffsets.length + quadCornerOrder[i];
            }
        }
        return indices;
    }

    private record Quad(Vector2f center, Vector4f color, int textureSlot, float tilingFactor) {}
}
