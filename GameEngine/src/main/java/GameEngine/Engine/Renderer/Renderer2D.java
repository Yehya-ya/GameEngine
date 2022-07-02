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

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

public class Renderer2D {
    public static Renderer2DStorage storage;

    public static void init() {
        YH_LOG_TRACE("Creating Renderer2D.");

        storage = new Renderer2DStorage();
        float[] vertices = { //
                0.5f, 0.5f, 0f, 1f, 1f, //
                -0.5f, 0.5f, 0f, 0f, 1f, //
                -0.5f, -0.5f, 0f, 0f, 0f, //
                0.5f, -0.5f, 0f, 1f, 0f, //
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
        storage.shader.delete();
        storage.whiteTexture.delete();
        storage.vertexArray.delete();
        YH_LOG_TRACE("Deleting Renderer2D.");
    }

    public static void begin(@NotNull Camera camera) {
        storage.shader.bind();
        storage.shader.UploadUniformMat4("uViewProjection", camera.getViewProjectionMatrix());
    }

    public static void end() {

    }

    public static void drawQuad(Vector2f pos, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(pos, 1.0f), size, color);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector2f size, Vector4f color) {
        Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, 1.0f);
        storage.shader.UploadUniformFloat4("uColor", color);
        storage.shader.UploadUniformFloat("uTilingFactor", 1.0f);
        storage.shader.UploadUniformMat4("uTransformation", transformation);

        storage.whiteTexture.bind(0);
        storage.vertexArray.bind();
        RendererCommandAPI.drawIndexed(storage.vertexArray);
        RendererStatistics.getInstance().addOneToQuadsCount();
        RendererStatistics.getInstance().addOneToDrawCallsCount();
    }

    public static void drawQuad(Vector2f pos, Vector2f size, Texture texture) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture, 1.0f);
    }

    public static void drawQuad(Vector2f pos, Vector2f size, Texture texture, float tilingFactor) {
        drawQuad(new Vector3f(pos, 1.0f), size, texture, tilingFactor);
    }

    public static void drawQuad(Vector3f pos, Vector2f size, Texture texture) {
        drawQuad(pos, size, texture, 1.0f);
    }

    public static void drawQuad(Vector3f pos, @NotNull Vector2f size, @NotNull Texture texture, float tilingFactor) {
        Matrix4f transformation = new Matrix4f().translate(pos).scale(size.x, size.y, 1.0f);
        storage.shader.UploadUniformFloat4("uColor", new Vector4f(1.0f));
        storage.shader.UploadUniformFloat("uTilingFactor", tilingFactor);
        storage.shader.UploadUniformMat4("uTransformation", transformation);

        texture.bind(0);
        storage.vertexArray.bind();
        RendererCommandAPI.drawIndexed(storage.vertexArray);
        RendererStatistics.getInstance().addOneToQuadsCount();
        RendererStatistics.getInstance().addOneToDrawCallsCount();
    }

    public static void drawRotatedQuad(Vector2f pos, Vector2f size, float rotationAngle, Vector4f color) {
        drawRotatedQuad(new Vector3f(pos, 1.0f), size, rotationAngle, color);
    }

    public static void drawRotatedQuad(Vector3f pos, @NotNull Vector2f size, float rotationAngle, Vector4f color) {
        Matrix4f transformation = new Matrix4f().translate(pos).rotate(rotationAngle, new Vector3f(0.0f,0.0f,1.0f)).scale(size.x, size.y, 1.0f);
        storage.shader.UploadUniformFloat4("uColor", color);
        storage.shader.UploadUniformFloat("uTilingFactor", 1.0f);
        storage.shader.UploadUniformMat4("uTransformation", transformation);

        storage.whiteTexture.bind(0);
        storage.vertexArray.bind();
        RendererCommandAPI.drawIndexed(storage.vertexArray);
        RendererStatistics.getInstance().addOneToQuadsCount();
        RendererStatistics.getInstance().addOneToDrawCallsCount();
    }

    public static void drawRotatedQuad(Vector2f pos, Vector2f size, float rotationAngle, Texture texture) {
        drawRotatedQuad(new Vector3f(pos, 1.0f), size, rotationAngle, texture, 1.0f);
    }

    public static void drawRotatedQuad(Vector2f pos, Vector2f size, float rotationAngle, Texture texture, float tilingFactor) {
        drawRotatedQuad(new Vector3f(pos, 1.0f), size, rotationAngle, texture, tilingFactor);
    }

    public static void drawRotatedQuad(Vector3f pos, Vector2f size, float rotationAngle, Texture texture) {
        drawRotatedQuad(pos, size, rotationAngle, texture, 1.0f);
    }

    public static void drawRotatedQuad(Vector3f pos, @NotNull Vector2f size, float rotationAngle, @NotNull Texture texture, float tilingFactor) {
        Matrix4f transformation = new Matrix4f().translate(pos).rotate(rotationAngle, new Vector3f(0.0f,0.0f,1.0f)).scale(size.x, size.y, 1.0f);
        storage.shader.UploadUniformFloat4("uColor", new Vector4f(1.0f));
        storage.shader.UploadUniformFloat("uTilingFactor", tilingFactor);
        storage.shader.UploadUniformMat4("uTransformation", transformation);

        texture.bind(0);
        storage.vertexArray.bind();
        RendererCommandAPI.drawIndexed(storage.vertexArray);
        RendererStatistics.getInstance().addOneToQuadsCount();
        RendererStatistics.getInstance().addOneToDrawCallsCount();
    }

    public static class Renderer2DStorage {
        public VertexArray vertexArray;
        public ShaderProgram shader;
        public Texture whiteTexture;
    }
}
