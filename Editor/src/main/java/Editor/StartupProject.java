package Editor;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.Core.ProjectSerializer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class StartupProject {

    private StartupProject() {}

    static @Nullable Path choose(@NotNull String[] args, @NotNull List<Path> recentProjectFiles) {
        boolean isProjectGiven = args.length > 0;
        if (isProjectGiven) {
            return projectFileFrom(Path.of(args[0]));
        }
        return recentProjectFiles.isEmpty() ? null : recentProjectFiles.getFirst();
    }

    private static @Nullable Path projectFileFrom(@NotNull Path projectPath) {
        if (!Files.exists(projectPath)) {
            YH_LOG_ERROR("`{}` does not exist.", projectPath.toAbsolutePath().normalize());
            return null;
        }
        if (!Files.isDirectory(projectPath)) {
            return projectPath;
        }

        Path directory = projectPath.toAbsolutePath().normalize();
        List<Path> projectFiles = ProjectSerializer.findProjectFiles(directory);
        if (projectFiles.size() == 1) {
            return projectFiles.getFirst();
        }
        if (projectFiles.isEmpty()) {
            YH_LOG_ERROR("There is no `{}` project file in `{}`.", Project.fileExtension, directory);
        } else {
            YH_LOG_ERROR(
                    "`{}` holds several project files: {}. Pass the one to open.",
                    directory,
                    projectFiles.stream().map(Path::getFileName).toList());
        }
        return null;
    }
}
