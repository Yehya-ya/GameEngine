package Editor;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.Core.ProjectSerializer;
import GameEngine.Engine.Utils.SettingsDirectory;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        RecentProjects recentProjects =
                RecentProjects.load(SettingsDirectory.file(Editor.applicationName, RecentProjects.fileName));
        Path projectFile = StartupProject.choose(args, recentProjects.getProjectFiles());
        Project project = projectFile != null ? ProjectSerializer.deserialize(projectFile) : null;

        Editor editor = new Editor(recentProjects, project);
        editor.run();
    }
}
