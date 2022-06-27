import Engine.Core.Application;
import Engine.Core.Layer;

public class Sandbox extends Application {
    private final Layer exampleLayer;

    public Sandbox() {
        exampleLayer = new ExampleLayer2D();
        pushLayer(exampleLayer);
    }

    @Override
    public void shutdown() {
        exampleLayer.delete();
    }
}
