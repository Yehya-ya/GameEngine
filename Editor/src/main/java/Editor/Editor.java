package Editor;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_INFO;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_WARN;

import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Core.Project;
import GameEngine.Engine.Utils.SettingsDirectory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Editor extends Application implements ProjectSwitcher {
    public static final String applicationName = "Editor";
    private final RecentProjects recentProjects;
    private final ImGuiLayer imGuiLayer;
    private @Nullable Layer projectLayer;

    public Editor(@NotNull RecentProjects recentProjects, @Nullable Project project) {
        super(applicationName);
        this.recentProjects = recentProjects;
        imGuiLayer = new ImGuiLayer(getWindow().getWindowID(), SettingsDirectory.file(applicationName, "imgui.ini"));
        pushOverlay(imGuiLayer);
        if (project != null) {
            openProject(project);
        } else {
            showProjectPicker();
        }
    }

    @Override
    public void openProject(@NotNull Project project) {
        YH_LOG_INFO("Opening the project `{}` in `{}`.", project.getName(), project.getDirectory());
        if (!Files.isDirectory(project.getAssetDirectory())) {
            YH_LOG_WARN(
                    "The project has no `{}` folder.",
                    project.getAssetDirectory().getFileName());
        }
        recentProjects.add(project.getFile());
        getWindow().setTitle(project.getName() + " - " + applicationName);
        replaceProjectLayer(() -> new EditorLayer(this, imGuiLayer, project, this));
    }

    @Override
    public void closeProject() {
        YH_LOG_INFO("Closing the project.");
        showProjectPicker();
    }

    @Override
    public @NotNull List<Path> recentProjectFiles() {
        return recentProjects.getProjectFiles();
    }

    private void showProjectPicker() {
        getWindow().setTitle(applicationName);
        replaceProjectLayer(() -> new ProjectPickerLayer(this));
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
    protected void beginGui() {
        imGuiLayer.begin();
    }

    @Override
    protected void endGui() {
        imGuiLayer.end();
    }

    @Override
    public void shutdown() {
        if (projectLayer != null) {
            projectLayer.delete();
        }
        imGuiLayer.delete();
    }
}
