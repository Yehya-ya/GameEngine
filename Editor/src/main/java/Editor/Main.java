package Editor;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_INFO;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_WARN;

import GameEngine.Engine.Core.Project;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        Project project = new Project(projectDirectoryFrom(args));
        YH_LOG_INFO("Opening the project in `{}`.", project.getDirectory());
        if (!Files.isDirectory(project.getAssetDirectory())) {
            YH_LOG_WARN(
                    "The project has no `{}` folder. Pass a project folder as the first argument.",
                    project.getAssetDirectory().getFileName());
        }

        Editor editor = new Editor(project);
        editor.run();
    }

    private static Path projectDirectoryFrom(String[] args) {
        boolean isProjectGiven = args.length > 0;
        return isProjectGiven ? Path.of(args[0]) : Path.of("");
    }
}
