package GameEngine.Engine.Renderer;

import GameEngine.Engine.Renderer.Buffer.BufferLayout;
import GameEngine.Engine.Renderer.Buffer.IndexBuffer;
import GameEngine.Engine.Renderer.Buffer.VertexBuffer;
import GameEngine.Engine.Renderer.Camera.Camera;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.stream.IntStream;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_DEBUG;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

public class BatchRenderer2D {
    public static Renderer2DStorage storage;

    public static void init() {
        YH_LOG_TRACE("Creating BatchRenderer2D.");

        storage = new Renderer2DStorage();

        // quads
        int[] indices = new int[Renderer2DStorage.MaxIndices];
        for (int i = 0, offset = 0; i < Renderer2DStorage.MaxIndices; i += 6, offset += 4) {
            indices[i] = offset;
            indices[i + 1] = offset + 1;
            indices[i + 2] = offset + 2;
            indices[i + 3] = offset + 2;
            indices[i + 4] = offset + 3;
            indices[i + 5] = offset;
        }
        storage.quadVertexArray = VertexArray.create();
        storage.quadVertexBuffer = VertexBuffer.create(QuadVertices.MaxFloats * Float.BYTES);
        storage.quadVertexBuffer.setLayout(new BufferLayout()//
                .addBufferElement(BufferLayout.ShaderDataType.Float3, "a_Position", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float4, "a_Color", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float2, "a_TexCoord", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float, "a_TexIndex", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float, "a_TilingFactor", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float, "a_EntityID", false));
        storage.quadVertexArray.addVertexBuffer(storage.quadVertexBuffer);
        storage.quadVertexArray.setIndexBuffer(IndexBuffer.create(indices));

        // circles
        storage.circleVertexArray = VertexArray.create();

        storage.circleVertexBuffer = VertexBuffer.create(CircleVertices.MaxFloats * Float.BYTES);
        storage.circleVertexBuffer.setLayout(new BufferLayout()//
                .addBufferElement(BufferLayout.ShaderDataType.Float3, "a_WorldPosition", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float3, "a_LocalPosition", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float4, "a_Color", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float, "a_Thickness", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float, "a_Fade", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float, "a_EntityID", false)
        );
        storage.circleVertexArray.addVertexBuffer(storage.circleVertexBuffer);
        storage.circleVertexArray.setIndexBuffer(IndexBuffer.create(indices)); // Use quad IB

        Texture whiteTexture = Texture.create("assets/textures/white.png");
        int[] texturesUniform = IntStream.range(0, Renderer2DStorage.MaxTextures).toArray();

        storage.quadShader = ShaderProgram.create("assets/shaders/quadShader.glsl");
        storage.textures[0] = whiteTexture;
        storage.circleShader = ShaderProgram.create("assets/shaders/circleShader.glsl");

        storage.baseVertices[0] = new Vector3f(-0.5f, -0.5f, 0.0f);
        storage.baseVertices[1] = new Vector3f(0.5f, -0.5f, 0.0f);
        storage.baseVertices[2] = new Vector3f(0.5f, 0.5f, 0.0f);
        storage.baseVertices[3] = new Vector3f(-0.5f, 0.5f, 0.0f);

        storage.baseCoords[0] = new Vector2f(0.0f, 0.0f);
        storage.baseCoords[1] = new Vector2f(1.0f, 0.0f);
        storage.baseCoords[2] = new Vector2f(1.0f, 1.0f);
        storage.baseCoords[3] = new Vector2f(0.0f, 1.0f);

        storage.cameraUniformBuffer = UniformBuffer.create(Renderer2DStorage.CameraBufferSize, 0);
        storage.quadVertices = new QuadVertices();
        storage.circleVertices = new CircleVertices();
    }

    public static void shutdown() {
        storage.quadVertexArray.delete();
        storage.circleVertexArray.delete();
        storage.quadShader.delete();
        storage.circleShader.delete();
        for (Texture texture: storage.textures) {
            if (texture != null) {
                texture.delete();
            }
        }

        YH_LOG_TRACE("Deleting BatchRenderer2D.");
    }

    public static void begin(Camera camera) {
        if (camera == null) {
            return;
        }
        float[] arr = new float[16];
        camera.getViewProjectionMatrix().get(arr);

        storage.cameraUniformBuffer.setData(arr);

        startBatch();
    }

    public static void end() {
        flush();
    }

    public static void startBatch() {
        storage.quadVertices.verticesCount = 0;
        storage.quadVertices.indexCount = 0;
        storage.circleVertices.verticesCount = 0;
        storage.circleVertices.indexCount = 0;
        storage.texturesIndex = 1;
    }

    public static void flush() {
        storage.quadVertexBuffer.setData(storage.quadVertices.getBuffer());
        for (int i = 0; i < storage.texturesIndex; i++) {
            storage.textures[i].bind(i);
        }
        storage.quadShader.bind();
        RendererCommandAPI.drawIndexed(storage.quadVertexArray, storage.quadVertices.indexCount);
        RendererStatistics.getInstance().addOneToDrawCallsCount();

        storage.circleVertexBuffer.setData(storage.circleVertices.getBuffer());
        storage.circleShader.bind();
        RendererCommandAPI.drawIndexed(storage.circleVertexArray, storage.circleVertices.indexCount);
        RendererStatistics.getInstance().addOneToDrawCallsCount();
    }

    public static void nextBatch() {
        flush();
        startBatch();
    }

    public static void drawQuad(Vector2f pos, Vector3f size, Vector4f color) {
        drawQuad(new Vector3f(pos, 1.0f), size, color, -1);
    }

    public static void drawQuad(Vector2f pos, Vector3f size, Vector4f color, int entityId) {
        drawQuad(new Vector3f(pos, 1.0f), size, color, entityId);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector3f size, Vector4f color, int entityId) {
        Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, size.z);

        drawWithColor(transformation, color, entityId);
    }

    public static void drawQuad(Vector2f pos, Vector3f size, Texture texture, int entityId) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture, 1.0f, entityId);
    }

    public static void drawQuad(Vector3f pos, Vector3f size, Texture texture, int entityId) {
        drawQuad(pos, size, texture, 1.0f, entityId);
    }

    public static void drawQuad(Vector2f pos, Vector3f size, Texture texture, float tilingFactor, int entityId) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture, tilingFactor, entityId);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector3f size, Texture texture, float tilingFactor, int entityId) {
        Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, size.z);

        drawWithTexture(transformation, texture, tilingFactor, entityId);
    }

    public static void drawRotatedQuad(Vector2f pos, Vector3f size, Vector3f rotation, Vector4f color, int entityId) {
        drawRotatedQuad(new Vector3f(pos, 1.0f), size, rotation, color, entityId);
    }

    public static void drawRotatedQuad(Vector3f pos, @NotNull Vector3f size, @NotNull Vector3f rotation, Vector4f color, int entityId) {
        Matrix4f transformation = new Matrix4f().translate(pos).rotateAffineXYZ(rotation.x, rotation.y, rotation.z).scale(size.x, size.y, size.z);

        drawWithColor(transformation, color, entityId);
    }

    public static void drawRotatedQuad(Vector2f pos, Vector3f size, Vector3f rotation, Texture texture, int entityId) {
        drawRotatedQuad(new Vector3f(pos, 1.0f), size, rotation, texture, 1.0f, entityId);
    }

    public static void drawRotatedQuad(Vector3f pos, Vector3f size, Vector3f rotation, Texture texture, int entityId) {
        drawRotatedQuad(pos, size, rotation, texture, 1.0f, entityId);
    }

    public static void drawRotatedQuad(Vector2f pos, Vector3f size, Vector3f rotation, Texture texture, float tilingFactor, int entityId) {
        drawRotatedQuad(new Vector3f(pos, 1.0f), size, rotation, texture, tilingFactor, entityId);
    }

    public static void drawRotatedQuad(Vector3f pos, @NotNull Vector3f size, @NotNull Vector3f rotation, Texture texture, float tilingFactor, int entityId) {
        Matrix4f transformation = new Matrix4f().translate(pos).rotateAffineXYZ(rotation.x, rotation.y, rotation.z).scale(size.x, size.y, size.z);

        drawWithTexture(transformation, texture, tilingFactor, entityId);
    }

    private static void drawWithColor(@NotNull Matrix4f transformation, Vector4f color, int entityId) {
        if (storage.quadVertices.indexCount >= Renderer2DStorage.MaxIndices) {
            nextBatch();
        }

        for (int i = 0; i < storage.baseVertices.length; i++) {
            storage.quadVertices.add(transformation.transformPosition(new Vector3f(storage.baseVertices[i])), color, storage.baseCoords[i], 1.0f, 0.0f, entityId);
        }
        storage.quadVertices.indexCount += 6;

        RendererStatistics.getInstance().addOneToQuadsCount();
    }


    private static void drawWithTexture(Matrix4f transformation, Texture texture, float tilingFactor, int entityId) {
        if (storage.quadVertices.indexCount >= Renderer2DStorage.MaxIndices) {
            nextBatch();
        }

        Vector4f color = new Vector4f(1.0f);

        float textureIndex = 0.0f;
        for (int i = 0; i < storage.texturesIndex; i++) {
            if (texture.equals(storage.textures[i])) {
                textureIndex = (float) i;
                break;
            }
        }

        if (textureIndex == 0.0f) {
            if (storage.texturesIndex >= Renderer2DStorage.MaxTextures) {
                nextBatch();
            }
            storage.textures[storage.texturesIndex] = texture;
            textureIndex = storage.texturesIndex;
            storage.texturesIndex++;
        }

        for (int i = 0; i < storage.baseVertices.length; i++) {
            storage.quadVertices.add(transformation.transformPosition(new Vector3f(storage.baseVertices[i])), color, storage.baseCoords[i], tilingFactor, textureIndex, entityId);
        }
        storage.quadVertices.indexCount += 6;

        RendererStatistics.getInstance().addOneToQuadsCount();
    }

    void drawCircle(Matrix4f transformation, Vector4f color, float thickness, float fade, int entityId) {
        // TODO: implement for circles
        // if (s_Data.QuadIndexCount >= Renderer2DData::MaxIndices)
        // 	NextBatch();

        for (int i = 0; i < storage.baseVertices.length; i++) {
            storage.circleVertices.add(transformation.transformPosition(new Vector3f(storage.baseVertices[i])), new Vector3f(storage.baseVertices[i]).mul(2.0f), color, thickness, fade, entityId);
        }

        storage.circleVertices.indexCount += 6;

        RendererStatistics.getInstance().addOneToQuadsCount();
    }

    public static class Renderer2DStorage {
        public static final int MaxQuads = 2000;
        public static final int MaxVertices = MaxQuads * 4;
        public static final int MaxIndices = MaxQuads * 6;
        public static final int MaxTextures = 32;
        public static final int CameraBufferSize = 4 * 4 * Float.BYTES;
        public VertexArray quadVertexArray;
        public VertexBuffer quadVertexBuffer;
        public ShaderProgram quadShader;
        public VertexArray circleVertexArray;
        public VertexBuffer circleVertexBuffer;
        public ShaderProgram circleShader;
        public QuadVertices quadVertices;
        public CircleVertices circleVertices;
        public Texture[] textures = new Texture[MaxTextures];
        public int texturesIndex = 1;
        public Vector3f[] baseVertices = new Vector3f[4];
        public Vector2f[] baseCoords = new Vector2f[4];
        public UniformBuffer cameraUniformBuffer;
    }

    public static class QuadVertices {
        public static final int VertexFloatCounts = 12;
        public static final int MaxFloats = Renderer2DStorage.MaxVertices * VertexFloatCounts;
        public static final int MaxBytes = MaxFloats * Float.BYTES;
        public float[] vertices;
        public int verticesCount;
        public int indexCount;

        public QuadVertices() {
            vertices = new float[MaxFloats];
            indexCount = 0;
            verticesCount = 0;
        }

        public void add(@NotNull Vector3f position, @NotNull Vector4f color, @NotNull Vector2f texCoord, float TilingFactor, float TextureIndex, int entityId) {
            YH_LOG_DEBUG(""+TextureIndex);
            vertices[verticesCount] = position.x;
            vertices[verticesCount + 1] = position.y;
            vertices[verticesCount + 2] = position.z;
            vertices[verticesCount + 3] = color.x;
            vertices[verticesCount + 4] = color.y;
            vertices[verticesCount + 5] = color.z;
            vertices[verticesCount + 6] = color.w;
            vertices[verticesCount + 7] = texCoord.x;
            vertices[verticesCount + 8] = texCoord.y;
            vertices[verticesCount + 9] = TextureIndex;
            vertices[verticesCount + 10] = TilingFactor;
            vertices[verticesCount + 11] = (float) entityId;

            verticesCount += VertexFloatCounts;
        }

        public float[] getBuffer() {
            float[] floatArray = new float[verticesCount];
            System.arraycopy(vertices, 0, floatArray, 0, verticesCount);
            return floatArray;
        }
    }

    public static class CircleVertices {
        public static final int VertexFloatCounts = 13;
        public static final int MaxFloats = Renderer2DStorage.MaxVertices * VertexFloatCounts;
        public static final int MaxBytes = MaxFloats * 4;
        public float[] vertices;
        public int verticesCount;
        public int indexCount;

        public CircleVertices() {
            vertices = new float[MaxFloats];
            indexCount = 0;
            verticesCount = 0;
        }

        public void add(@NotNull Vector3f wordPosition, @NotNull Vector3f localPosition, @NotNull Vector4f color, float thickness, float fade, int entityId) {
            vertices[verticesCount] = wordPosition.x;
            vertices[verticesCount + 1] = wordPosition.y;
            vertices[verticesCount + 2] = wordPosition.z;
            vertices[verticesCount + 3] = localPosition.x;
            vertices[verticesCount + 4] = localPosition.y;
            vertices[verticesCount + 5] = localPosition.z;
            vertices[verticesCount + 6] = color.x;
            vertices[verticesCount + 7] = color.y;
            vertices[verticesCount + 8] = color.z;
            vertices[verticesCount + 9] = color.w;
            vertices[verticesCount + 10] = thickness;
            vertices[verticesCount + 11] = fade;
            vertices[verticesCount + 12] = (float) entityId;

            verticesCount += VertexFloatCounts;
        }

        public float[] getBuffer() {
            float[] floatArray = new float[verticesCount];
            System.arraycopy(vertices, 0, floatArray, 0, verticesCount);
            return floatArray;
        }
    }
}
