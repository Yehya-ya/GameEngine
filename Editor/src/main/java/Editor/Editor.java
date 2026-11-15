package Editor;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_INFO;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_WARN;

import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Core.Project;
import java.nio.file.Files;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Editor extends Application {
    public static final String applicationName = "Editor";
    private final RecentProjects recentProjects;
    private @Nullable Layer projectLayer;

    public Editor(@NotNull RecentProjects recentProjects, @Nullable Project project) {
        super(applicationName);
        this.recentProjects = recentProjects;
        if (project != null) {
            openProject(project);
        } else {
            showProjectPicker();
        }
    }

    public void openProject(@NotNull Project project) {
        YH_LOG_INFO("Opening the project `{}` in `{}`.", project.getName(), project.getDirectory());
        if (!Files.isDirectory(project.getAssetDirectory())) {
            YH_LOG_WARN(
                    "The project has no `{}` folder.",
                    project.getAssetDirectory().getFileName());
        }
        recentProjects.add(project.getFile());
        getWindow().setTitle(project.getName() + " - " + applicationName);
        replaceProjectLayer(() -> new EditorLayer(project));
    }

    private void showProjectPicker() {
        getWindow().setTitle(applicationName);
        replaceProjectLayer(() -> new ProjectPickerLayer(recentProjects, this::openProject));
    }

    private void replaceProjectLayer(@NotNull Supplier<Layer> createLayer) {
        runBeforeNextFrame(() -> {
            if (projectLayer != null) {
                popLayer(projectLayer);
                projectLayer.delete();
            }
            projectLayer = createLayer.get();
            pushLayer(projectLayer);
        });
    }

    @Override
    public void shutdown() {
        if (projectLayer != null) {
            projectLayer.delete();
        }
    }
}
