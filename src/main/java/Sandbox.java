import Engine.Core.Application;

public class Sandbox extends Application {
    private final ExampleLayer exampleLayer;

    public Sandbox() {
        exampleLayer = new ExampleLayer();
        pushLayer(exampleLayer);
    }

    @Override
    public void shutdown() {
        exampleLayer.delete();
    }
}
