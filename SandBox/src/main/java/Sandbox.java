import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;

public class Sandbox extends Application {
    private final Layer exampleLayer;

    public Sandbox() {
        super("SandBox");
        exampleLayer = new ExampleLayer2DBatchRenderer(getRenderer2D());
        pushLayer(exampleLayer);
    }

    @Override
    public void shutdown() {
        exampleLayer.delete();
    }
}
