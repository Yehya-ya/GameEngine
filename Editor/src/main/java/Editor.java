import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Core.Project;

public class Editor extends Application {
    private final Layer exampleLayer;

    public Editor(Project project) {
        super("Editor");
        exampleLayer = new EditorLayer(project);
        pushLayer(exampleLayer);
    }

    @Override
    public void shutdown() {
        exampleLayer.delete();
    }
}
