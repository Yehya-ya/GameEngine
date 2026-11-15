package Editor;

import GameEngine.Engine.Core.Project;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface ProjectSwitcher {
    void openProject(@NotNull Project project);

    void closeProject();

    @NotNull
    List<Path> recentProjectFiles();
}
