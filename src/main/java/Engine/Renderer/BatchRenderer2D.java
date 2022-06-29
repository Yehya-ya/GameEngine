package Engine.Renderer;

import Engine.Renderer.Buffer.BufferLayout;
import Engine.Renderer.Buffer.IndexBuffer;
import Engine.Renderer.Buffer.VertexBuffer;
import Engine.Renderer.Camera.Camera;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

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
        storage.vertexBuffer = VertexBuffer.create(QuadVertices.MaxVertices * QuadVertices.VerticesSize);
        storage.vertexBuffer.setLayout(new BufferLayout()//
                .addBufferElement(BufferLayout.ShaderDataType.Float3, "aPos", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float4, "aColor", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float2, "aTexCoord", false));
        storage.vertexArray.addVertexBuffer(storage.vertexBuffer);
        storage.vertexArray.setIndexBuffer(IndexBuffer.create(indices));

        storage.whiteTexture = Texture.create("assets/textures/white.png");

        storage.shader = ShaderProgram.create("assets/shaders/shader2DBatchRenderer.glsl");
        storage.shader.bind();
        storage.shader.UploadUniformInt("uTexture", 0);
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
    }

    public static void end() {
        storage.vertexBuffer.setData(storage.quadVertices.getBuffer());

        flush();
    }

    public static void flush() {
        RendererCommandAPI.drawIndexed(storage.quadVertices.quadIndexCount);
    }

    public static void drawQuad(Vector2f pos, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(pos, 1.0f), size, color);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector2f size, Vector4f color) {
        storage.quadVertices.add(pos, color, new Vector2f(0.0f, 0.0f));
        storage.quadVertices.add(new Vector3f(pos.x + size.x, pos.y, pos.z), color, new Vector2f(1.0f, 0.0f));
        storage.quadVertices.add(new Vector3f(pos.x + size.x, pos.y + size.y, pos.z), color, new Vector2f(1.0f, 1.0f));
        storage.quadVertices.add(new Vector3f(pos.x, pos.y + size.y, pos.z), color, new Vector2f(0.0f, 1.0f));

        storage.quadVertices.quadIndexCount += 6;
        /* Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, 1.0f);
        storage.shader.UploadUniformMat4("uTransformation", transformation);*/
    }

    public static void drawQuad(Vector2f pos, Vector2f size, Texture texture) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector2f size, @NotNull Texture texture) {
        storage.shader.UploadUniformFloat4("uColor", new Vector4f(1.0f));
        texture.bind();

        Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, 1.0f);
        storage.shader.UploadUniformMat4("uTransformation", transformation);
        storage.vertexArray.bind();
        RendererCommandAPI.drawIndexed(storage.vertexArray);
    }

    public static class Renderer2DStorage {
        public VertexArray vertexArray;
        public VertexBuffer vertexBuffer;
        public ShaderProgram shader;
        public Texture whiteTexture;

        public QuadVertices quadVertices;
    }

    public static class QuadVertices {
        public static final int MaxQuads = 10000;
        public static final int MaxVertices = MaxQuads * 4;
        public static final int MaxIndices = MaxQuads * 6;
        public static final int VerticesSize = 9 * Float.BYTES;
        public ArrayList<Float> vertices;
        public int quadIndexCount;

        public QuadVertices() {
            vertices = new ArrayList<>();
            quadIndexCount = 0;
        }

        public void add(@NotNull Vector3f position, @NotNull Vector4f color, @NotNull Vector2f texCoord) {
            vertices.add(position.x);
            vertices.add(position.y);
            vertices.add(position.z);
            vertices.add(color.x);
            vertices.add(color.y);
            vertices.add(color.z);
            vertices.add(color.w);
            vertices.add(texCoord.x);
            vertices.add(texCoord.y);
        }

        public float[] getBuffer() {
            Float[] array = new Float[vertices.size()];
            vertices.toArray(array);
            float[] floatArray = new float[vertices.size()];
            int i = 0;

            for (Float f : array) {
                floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
            }

            return floatArray;
        }
    }
}
