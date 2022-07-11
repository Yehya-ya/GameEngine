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

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

public class BatchRenderer2D {
    public static Renderer2DStorage storage;

    public static void init() {
        YH_LOG_TRACE("Creating BatchRenderer2D.");

        int[] indices = new int[QuadVertices.MaxIndices];
        for (int i = 0, offset = 0; i < QuadVertices.MaxIndices; i += 6, offset += 4) {
            indices[i] = offset;
            indices[i + 1] = offset + 1;
            indices[i + 2] = offset + 2;
            indices[i + 3] = offset + 2;
            indices[i + 4] = offset + 3;
            indices[i + 5] = offset;
        }

        storage = new Renderer2DStorage();
        storage.vertexArray = VertexArray.create();
        storage.vertexBuffer = VertexBuffer.create(QuadVertices.MaxFloats * Float.BYTES);
        storage.vertexBuffer.setLayout(new BufferLayout()//
                .addBufferElement(BufferLayout.ShaderDataType.Float3, "aPos", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float4, "aColor", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float2, "aTexCoord", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float, "aTilingFactor", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float, "aTextureIndex", false));
        storage.vertexArray.addVertexBuffer(storage.vertexBuffer);
        storage.vertexArray.setIndexBuffer(IndexBuffer.create(indices));

        storage.whiteTexture = Texture.create("assets/textures/white.png");
        storage.shader = ShaderProgram.create("assets/shaders/shader2DBatchRenderer.glsl");
        int[] texturesUniform = IntStream.range(0, QuadVertices.MaxTextures).toArray();

        storage.shader.bind();
        storage.shader.UploadUniformIntArray("uTextures", texturesUniform);
        storage.textures[0] = storage.whiteTexture;

        storage.baseVertices[0] = new Vector3f(-0.5f, -0.5f, 0.0f);
        storage.baseVertices[1] = new Vector3f(0.5f, -0.5f, 0.0f);
        storage.baseVertices[2] = new Vector3f(0.5f, 0.5f, 0.0f);
        storage.baseVertices[3] = new Vector3f(-0.5f, 0.5f, 0.0f);

        storage.baseCoords[0] = new Vector2f(0.0f, 0.0f);
        storage.baseCoords[1] = new Vector2f(1.0f, 0.0f);
        storage.baseCoords[2] = new Vector2f(1.0f, 1.0f);
        storage.baseCoords[3] = new Vector2f(0.0f, 1.0f);

        storage.quadVertices = new QuadVertices();
    }

    public static void shutdown() {
        storage.vertexArray.delete();
        storage.shader.delete();
        storage.whiteTexture.delete();
        YH_LOG_TRACE("Deleting BatchRenderer2D.");
    }

    public static void begin(Camera camera) {
        if (camera == null) {
            return;
        }

        storage.shader.bind();
        storage.shader.UploadUniformMat4("uViewProjection", camera.getViewProjectionMatrix());

        startBatch();
    }

    public static void end() {
        flush();
    }

    public static void startBatch() {
        storage.quadVertices.verticesCount = 0;
        storage.quadVertices.quadIndexCount = 0;
        storage.texturesIndex = 1;
    }

    public static void flush() {
        storage.vertexBuffer.setData(storage.quadVertices.getBuffer());

        for (int i = 0; i < storage.texturesIndex; i++) {
            storage.textures[i].bind(i);
        }
        RendererCommandAPI.drawIndexed(storage.quadVertices.quadIndexCount);
        RendererStatistics.getInstance().addOneToDrawCallsCount();
    }

    public static void nextBatch() {
        flush();
        startBatch();
    }

    public static void drawQuad(Vector2f pos, Vector3f size, Vector4f color) {
        drawQuad(new Vector3f(pos, 1.0f), size, color);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector3f size, Vector4f color) {
        Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, size.z);

        drawWithColor(transformation, color);
    }

    public static void drawQuad(Vector2f pos, Vector3f size, Texture texture) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture, 1.0f);
    }

    public static void drawQuad(Vector3f pos, Vector3f size, Texture texture) {
        drawQuad(pos, size, texture, 1.0f);
    }

    public static void drawQuad(Vector2f pos, Vector3f size, Texture texture, float tilingFactor) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture, tilingFactor);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector3f size, Texture texture, float tilingFactor) {
        Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, size.z);

        drawWithTexture(transformation, texture, tilingFactor);
    }

    public static void drawRotatedQuad(Vector2f pos, Vector3f size, Vector3f rotation, Vector4f color) {
        drawRotatedQuad(new Vector3f(pos, 1.0f), size, rotation, color);
    }

    public static void drawRotatedQuad(Vector3f pos, @NotNull Vector3f size, @NotNull Vector3f rotation, Vector4f color) {
        Matrix4f transformation = new Matrix4f().translate(pos).rotateAffineXYZ(rotation.x, rotation.y, rotation.z).scale(size.x, size.y, size.z);

        drawWithColor(transformation, color);
    }

    public static void drawRotatedQuad(Vector2f pos, Vector3f size, Vector3f rotation, Texture texture) {
        drawRotatedQuad(new Vector3f(pos, 1.0f), size, rotation, texture, 1.0f);
    }

    public static void drawRotatedQuad(Vector3f pos, Vector3f size, Vector3f rotation, Texture texture) {
        drawRotatedQuad(pos, size, rotation, texture, 1.0f);
    }

    public static void drawRotatedQuad(Vector2f pos, Vector3f size, Vector3f rotation, Texture texture, float tilingFactor) {
        drawRotatedQuad(new Vector3f(pos, 1.0f), size, rotation, texture, tilingFactor);
    }

    public static void drawRotatedQuad(Vector3f pos, @NotNull Vector3f size, @NotNull Vector3f rotation, Texture texture, float tilingFactor) {
        Matrix4f transformation = new Matrix4f().translate(pos).rotateAffineXYZ(rotation.x, rotation.y, rotation.z).scale(size.x, size.y, size.z);

        drawWithTexture(transformation, texture, tilingFactor);
    }

    private static void drawWithColor(@NotNull Matrix4f transformation, Vector4f color) {
        if (storage.quadVertices.quadIndexCount >= QuadVertices.MaxIndices) {
            nextBatch();
        }

        for (int i = 0; i < storage.baseVertices.length; i++) {
            storage.quadVertices.add(transformation.transformPosition(new Vector3f(storage.baseVertices[i])), color, storage.baseCoords[i], 1.0f, 0.0f);
        }
        storage.quadVertices.quadIndexCount += 6;

        RendererStatistics.getInstance().addOneToQuadsCount();
    }


    private static void drawWithTexture(Matrix4f transformation, Texture texture, float tilingFactor) {
        if (storage.quadVertices.quadIndexCount >= QuadVertices.MaxIndices) {
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
            if (storage.texturesIndex >= QuadVertices.MaxTextures) {
                nextBatch();
            }
            storage.textures[storage.texturesIndex] = texture;
            textureIndex = storage.texturesIndex;
            storage.texturesIndex++;
        }

        for (int i = 0; i < storage.baseVertices.length; i++) {
            storage.quadVertices.add(transformation.transformPosition(new Vector3f(storage.baseVertices[i])), color, storage.baseCoords[i], tilingFactor, textureIndex);
        }
        storage.quadVertices.quadIndexCount += 6;

        RendererStatistics.getInstance().addOneToQuadsCount();
    }

    public static class Renderer2DStorage {
        public VertexArray vertexArray;
        public VertexBuffer vertexBuffer;
        public ShaderProgram shader;
        public Texture whiteTexture;
        public QuadVertices quadVertices;
        public Texture[] textures = new Texture[QuadVertices.MaxTextures];
        public int texturesIndex = 1;
        public Vector3f[] baseVertices = new Vector3f[4];
        public Vector2f[] baseCoords = new Vector2f[4];
    }

    public static class QuadVertices {
        public static final int MaxQuads = 2000;
        public static final int VertexFloatCounts = 11;
        public static final int MaxVertices = MaxQuads * 4;
        public static final int MaxFloats = MaxVertices * VertexFloatCounts;
        public static final int MaxIndices = MaxQuads * 6;
        public static final int MaxTextures = 32;

        public float[] vertices;
        public int verticesCount;
        public int quadIndexCount;

        public QuadVertices() {
            vertices = new float[MaxFloats];
            quadIndexCount = 0;
            verticesCount = 0;
        }

        public void add(@NotNull Vector3f position, @NotNull Vector4f color, @NotNull Vector2f texCoord, float TilingFactor, float TextureIndex) {
            vertices[verticesCount] = position.x;
            vertices[verticesCount + 1] = position.y;
            vertices[verticesCount + 2] = position.z;
            vertices[verticesCount + 3] = color.x;
            vertices[verticesCount + 4] = color.y;
            vertices[verticesCount + 5] = color.z;
            vertices[verticesCount + 6] = color.w;
            vertices[verticesCount + 7] = texCoord.x;
            vertices[verticesCount + 8] = texCoord.y;
            vertices[verticesCount + 9] = TilingFactor;
            vertices[verticesCount + 10] = TextureIndex;

            verticesCount += VertexFloatCounts;
        }

        public float[] getBuffer() {
            float[] floatArray = new float[verticesCount];
            System.arraycopy(vertices, 0, floatArray, 0, verticesCount);
            return floatArray;
        }
    }
}
