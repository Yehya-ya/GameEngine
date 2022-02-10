package Engine.Renderer;

public class Renderer {
    private final RenderBatch batch;
    private final ShaderProgram shaderProgram;

    public Renderer() {
        this.batch = new RenderBatch();
        this.shaderProgram = new ShaderProgram("vertexShader", "fragmentShader");
        this.shaderProgram.compileAndLink();
    }

    public void init() {
        this.batch.init();
    }

    public void render() {
        this.shaderProgram.use();

        this.batch.render();

        this.shaderProgram.detach();
    }
}
