package Engine.Renderer;

import Engine.Renderer.Buffer.BufferLayout;
import Engine.Renderer.Buffer.IndexBuffer;
import Engine.Renderer.Buffer.VertexBuffer;
import Engine.Renderer.Camera.Camera;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.stream.IntStream;

import static Engine.Utils.YH_Log.YH_LOG_TRACE;

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
    }

    public static void shutdown() {
        storage.vertexArray.delete();
        storage.shader.delete();
        storage.whiteTexture.delete();
        YH_LOG_TRACE("Deleting BatchRenderer2D.");
    }

    public static void begin(@NotNull Camera camera) {
        storage.shader.bind();
        storage.shader.UploadUniformMat4("uViewProjection", camera.getViewProjectionMatrix());
        storage.quadVertices = new QuadVertices();
        storage.textures[0] = storage.whiteTexture;
    }

    public static void end() {
        storage.vertexBuffer.setData(storage.quadVertices.getBuffer());

        flush();
    }

    public static void flush() {
        for (int i = 0; i < storage.texturesIndex; i++) {
            storage.textures[i].bind(i);
        }
        RendererCommandAPI.drawIndexed(storage.quadVertices.quadIndexCount);
    }

    public static void drawQuad(Vector2f pos, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(pos, 1.0f), size, color);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector2f size, Vector4f color) {
        storage.quadVertices.add(pos, color, new Vector2f(0.0f, 0.0f), 1.0f, 0.0f);
        storage.quadVertices.add(new Vector3f(pos.x + size.x, pos.y, pos.z), color, new Vector2f(1.0f, 0.0f), 1.0f, 0.0f);
        storage.quadVertices.add(new Vector3f(pos.x + size.x, pos.y + size.y, pos.z), color, new Vector2f(1.0f, 1.0f), 1.0f, 0.0f);
        storage.quadVertices.add(new Vector3f(pos.x, pos.y + size.y, pos.z), color, new Vector2f(0.0f, 1.0f), 1.0f, 0.0f);
        storage.quadVertices.quadIndexCount += 6;
    }

    public static void drawQuad(Vector2f pos, Vector2f size, Texture texture) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture, 1.0f);
    }

    public static void drawQuad(Vector3f pos, Vector2f size, Texture texture) {
        drawQuad(pos, size, texture, 1.0f);
    }

    public static void drawQuad(Vector2f pos, Vector2f size, Texture texture, float tilingFactor) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture, tilingFactor);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector2f size, @NotNull Texture texture, float tilingFactor) {
        Vector4f color = new Vector4f(1.0f);

        float textureIndex = 0.0f;
        for (int i = 0; i < storage.texturesIndex; i++) {
            if (texture.equals(storage.textures[i])) {
                textureIndex = (float) i;
                break;
            }
        }

        if (textureIndex == 0.0f) {
            storage.textures[storage.texturesIndex] = texture;
            storage.texturesIndex++;
        }

        storage.quadVertices.add(pos, color, new Vector2f(0.0f, 0.0f), tilingFactor, textureIndex);
        storage.quadVertices.add(new Vector3f(pos.x + size.x, pos.y, pos.z), color, new Vector2f(1.0f, 0.0f), tilingFactor, textureIndex);
        storage.quadVertices.add(new Vector3f(pos.x + size.x, pos.y + size.y, pos.z), color, new Vector2f(1.0f, 1.0f), tilingFactor, textureIndex);
        storage.quadVertices.add(new Vector3f(pos.x, pos.y + size.y, pos.z), color, new Vector2f(0.0f, 1.0f), tilingFactor, textureIndex);
        storage.quadVertices.quadIndexCount += 6;
    }

    public static class Renderer2DStorage {
        public VertexArray vertexArray;
        public VertexBuffer vertexBuffer;
        public ShaderProgram shader;
        public Texture whiteTexture;
        public QuadVertices quadVertices;
        public Texture[] textures = new Texture[QuadVertices.MaxTextures];
        public int texturesIndex = 1;
    }

    public static class QuadVertices {
        public static final int MaxQuads = 100000;
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
