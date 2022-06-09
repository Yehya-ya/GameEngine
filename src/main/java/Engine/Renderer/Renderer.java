package Engine.Renderer;

import Engine.Renderer.Camera.Camera;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class Renderer {
    private static final SceneData sceneData = new SceneData();

    public static void beginScene(@NotNull Camera camera) {
        sceneData.viewProjectionMatrix = camera.getViewProjectionMatrix();
    }

    public static void endScene() {

    }

    public static void submit(@NotNull ShaderProgram shaderProgram, @NotNull VertexArray vertexArray, Matrix4f transformation) {
        shaderProgram.bind();
        shaderProgram.UploadUniformMat4("uViewProjection", sceneData.viewProjectionMatrix);
        shaderProgram.UploadUniformMat4("uTransformation", transformation);
        vertexArray.bind();
        RendererCommandAPI.drawIndexed(vertexArray);
    }

    public static class SceneData {
        public Matrix4f viewProjectionMatrix;
    }
}
