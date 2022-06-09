import Engine.Input;
import Engine.Layer;
import Engine.Renderer.Buffer.BufferLayout;
import Engine.Renderer.Buffer.IndexBuffer;
import Engine.Renderer.Buffer.VertexBuffer;
import Engine.Renderer.Camera.OrthographicCamera;
import Engine.Renderer.*;
import Engine.Utils.TimeStep;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static Engine.Utils.KeyCodes.*;

public class ExampleLayer extends Layer {
    private final OrthographicCamera camera;
    private final VertexArray vertexArray;
    private final ShaderLibrary shaderLibrary;
    private final ShaderProgram shaderProgram;

    private final float[] col;
    private final Matrix4f transformation;
    private final Vector3f cameraPos;

    public ExampleLayer() {
        super("Example");
        col = new float[4];
        col[3] = 1.0f;
        transformation = new Matrix4f().translate(new Vector3f(0.2f, 0.2f, 0.0f)).scale(0.1f);
        cameraPos = new Vector3f();
        camera = new OrthographicCamera(-1.6f, 1.6f, -0.9f, 0.9f);
        vertexArray = VertexArray.create();
        float[] vertices = {-0.5f, -0.5f, 0.5f, -0.5f, 0.0f, 0.5f};
        VertexBuffer vertexBuffer = VertexBuffer.create(vertices);
        BufferLayout bufferLayout = new BufferLayout().addBufferElement(BufferLayout.ShaderDataType.Float2, "aPos", false);
        vertexBuffer.setLayout(bufferLayout);
        vertexArray.addVertexBuffer(vertexBuffer);
        shaderLibrary = new ShaderLibrary();

        int[] indices = {0, 1, 2};

        IndexBuffer indexBuffer = IndexBuffer.create(indices);
        vertexArray.setIndexBuffer(indexBuffer);

        // shaderProgram = ShaderProgram.create("vertex/vertexShader.glsl", "fragment/fragmentShader.glsl");
        shaderProgram = shaderLibrary.load("shader.glsl");
        shaderProgram.bind();
    }

    @Override
    public void onUpdate(TimeStep timeStep) {
        if (Input.isKeyPressed(YH_KEY_W)) cameraPos.y += 0.5f * timeStep.getMilliseconds();
        if (Input.isKeyPressed(YH_KEY_S)) cameraPos.y -= 0.5f * timeStep.getMilliseconds();
        if (Input.isKeyPressed(YH_KEY_D)) cameraPos.x += 0.5f * timeStep.getMilliseconds();
        if (Input.isKeyPressed(YH_KEY_A)) cameraPos.x -= 0.5f * timeStep.getMilliseconds();

        camera.setPosition(cameraPos);
        Renderer.beginScene(camera);
        shaderProgram.UploadUniformFloat4("u_color", new Vector4f(col));
        Renderer.submit(shaderProgram, vertexArray, new Matrix4f());
        Renderer.endScene();
    }

    @Override
    public void onImgRender() {
        ImGui.colorPicker4("color: ", col);
    }
}
