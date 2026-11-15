package Editor;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_INFO;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_WARN;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.Core.ProjectSerializer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class Main {

    public static void main(String[] args) {
        Project project = openProject(projectPathFrom(args));
        if (project != null) {
            runEditor(project);
        } else {
            System.exit(1);
        }
    }

    private static void runEditor(Project project) {
        YH_LOG_INFO("Opening the project `{}` in `{}`.", project.getName(), project.getDirectory());
        if (!Files.isDirectory(project.getAssetDirectory())) {
            YH_LOG_WARN(
                    "The project has no `{}` folder.",
                    project.getAssetDirectory().getFileName());
        }

        Editor editor = new Editor(project);
        editor.run();
    }

    private static Path projectPathFrom(String[] args) {
        boolean isProjectGiven = args.length > 0;
        return isProjectGiven ? Path.of(args[0]) : Path.of("");
    }

    private static @Nullable Project openProject(Path projectPath) {
        Path projectFile = projectFileFrom(projectPath);
        return projectFile != null ? ProjectSerializer.deserialize(projectFile) : null;
    }

    private static @Nullable Path projectFileFrom(Path projectPath) {
        if (!Files.isDirectory(projectPath)) {
            return projectPath;
        }

        Path directory = projectPath.toAbsolutePath().normalize();
        List<Path> projectFiles = ProjectSerializer.findProjectFiles(directory);
        if (projectFiles.size() == 1) {
            return projectFiles.getFirst();
        }
        if (projectFiles.isEmpty()) {
            YH_LOG_ERROR(
                    "There is no `{}` project file in `{}`. Pass a project file, or the folder that holds it, as the first argument.",
                    Project.fileExtension,
                    directory);
        } else {
            YH_LOG_ERROR(
                    "`{}` holds several project files: {}. Pass the one to open as the first argument.",
                    directory,
                    projectFiles.stream().map(Path::getFileName).toList());
        }
        return null;
    }
}
