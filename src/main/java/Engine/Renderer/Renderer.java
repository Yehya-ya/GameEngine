package Engine.Renderer;

import Engine.Renderer.Camera.Camera;
import org.joml.Matrix4f;

public class Renderer {
    private static final SceneData sceneData = new SceneData();

    public static void beginScene(Camera camera) {
        sceneData.viewProjectionMatrix = camera.getViewProjectionMatrix();
    }

    public static void endScene() {

    }

    public static void submit(ShaderProgram shaderProgram, VertexArray vertexArray, Matrix4f transformation) {
        shaderProgram.bind();
        shaderProgram.UploadUniformMat4("uTransformation", transformation);
        vertexArray.bind();
        RendererCommandAPI.drawIndexed(vertexArray);
    }

    public static class SceneData {
        public Matrix4f viewProjectionMatrix;
    }
}
