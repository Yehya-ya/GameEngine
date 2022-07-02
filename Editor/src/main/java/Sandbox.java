import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;

public class Sandbox extends Application {
    private final Layer exampleLayer;

    public Sandbox() {
        exampleLayer = new ExampleLayer();
        pushLayer(exampleLayer);
    }

    @Override
    public void shutdown() {
        exampleLayer.delete();
    }
}
