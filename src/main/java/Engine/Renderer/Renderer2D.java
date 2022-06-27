package Engine.Renderer;

import Engine.Renderer.Buffer.BufferLayout;
import Engine.Renderer.Buffer.IndexBuffer;
import Engine.Renderer.Buffer.VertexBuffer;
import Engine.Renderer.Camera.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Renderer2D {
    public static Renderer2DStorage storage;

    public static void init() {
        storage = new Renderer2DStorage();
        float[] vertices = { //
                1f, 1f, 0f, 1f, 1f, //
                -1f, 1f, 0f, 0f, 1f, //
                -1f, -1f, 0f, 0f, 0f, //
                1f, -1f, 0f, 1f, 0f, //
        };

        int[] indices = { //
                0, 1, 2, //
                0, 2, 3, //
        };

        storage.vertexArray = VertexArray.create();
        VertexBuffer vertexBuffer = VertexBuffer.create(vertices);
        vertexBuffer.setLayout(new BufferLayout()//
                .addBufferElement(BufferLayout.ShaderDataType.Float3, "aPos", false)//
                .addBufferElement(BufferLayout.ShaderDataType.Float2, "aTexCoord", false));
        storage.vertexArray.addVertexBuffer(vertexBuffer);
        storage.vertexArray.setIndexBuffer(IndexBuffer.create(indices));


        storage.whiteTexture = Texture.create("assets/textures/white.png");

        storage.shader = ShaderProgram.create("assets/shaders/shader2D.glsl");
        storage.shader.bind();
        storage.shader.UploadUniformInt("uTexture", 0);

    }

    public static void shutdown() {
        storage.vertexArray.delete();
        storage.shader.delete();
        storage.whiteTexture.delete();
    }

    public static void begin(Camera camera) {
        storage.shader.bind();
        storage.shader.UploadUniformMat4("uViewProjection", camera.getViewProjectionMatrix());
    }

    public static void end() {

    }

    public static void drawQuad(Vector2f pos, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(pos, 1.0f), size, color);
    }


    public static void drawQuad(Vector3f pos, Vector2f size, Vector4f color) {
        storage.whiteTexture.bind();
        storage.shader.UploadUniformFloat4("uColor", color);

        Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, 1.0f);
        storage.shader.UploadUniformMat4("uTransformation", transformation);
        storage.vertexArray.bind();
        RendererCommandAPI.drawIndexed(storage.vertexArray);
    }


    public static void drawQuad(Vector2f pos, Vector2f size, Texture texture) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture);
    }


    public static void drawQuad(Vector3f pos, Vector2f size, Texture texture) {
        storage.shader.UploadUniformFloat4("uColor", new Vector4f(1.0f));
        texture.bind();

        Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, 1.0f);
        storage.shader.UploadUniformMat4("uTransformation", transformation);
        storage.vertexArray.bind();
        RendererCommandAPI.drawIndexed(storage.vertexArray);
    }

    public static class Renderer2DStorage {
        public VertexArray vertexArray;
        public ShaderProgram shader;
        public Texture whiteTexture;
    }
}
