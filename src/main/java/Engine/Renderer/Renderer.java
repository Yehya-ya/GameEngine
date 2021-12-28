package Engine.Renderer;

public class Renderer {
    private final RenderBatch batch;
    private final Shader shader;

    public Renderer() {
        this.batch = new RenderBatch();
        this.shader = new Shader("shader.glsl");
        this.shader.compile();
    }

    public void init() {
        this.batch.init();
    }

    public void render() {
        this.shader.use();

        this.batch.render();

        this.shader.detach();
    }
}
