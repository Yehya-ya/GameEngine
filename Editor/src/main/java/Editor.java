import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Core.Project;

public class Editor extends Application {
    private final Layer editorLayer;

    public Editor(Project project) {
        super("Editor");
        editorLayer = new EditorLayer(project);
        pushLayer(editorLayer);
    }

    @Override
    public void shutdown() {
        editorLayer.delete();
    }
}
