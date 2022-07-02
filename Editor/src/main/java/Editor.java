import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;

public class Editor extends Application {
    private final Layer exampleLayer;

    public Editor() {
        exampleLayer = new EditorLayer();
        pushLayer(exampleLayer);
    }

    @Override
    public void shutdown() {
        exampleLayer.delete();
    }
}
