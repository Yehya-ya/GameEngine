package Editor;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.Core.ProjectSerializer;
import GameEngine.Engine.Utils.LogFiles;
import GameEngine.Engine.Utils.SettingsDirectory;
import Utils.MessageBox;
import java.nio.file.Path;

public class Main {
    private static final String logFolderName = "logs";

    public static void main(String[] args) {
        Path logDirectory = SettingsDirectory.file(Editor.applicationName, logFolderName);
        LogFiles.writeTo(logDirectory);
        try {
            runEditor(args);
        } catch (Throwable failure) {
            reportFailure(failure, logDirectory);
            System.exit(1);
        }
    }

    private static void runEditor(String[] args) {
        RecentProjects recentProjects =
                RecentProjects.load(SettingsDirectory.file(Editor.applicationName, RecentProjects.fileName));
        Path projectFile = StartupProject.choose(args, recentProjects.getProjectFiles());
        Project project = projectFile != null ? ProjectSerializer.deserialize(projectFile) : null;

        Editor editor = new Editor(recentProjects, project);
        editor.run();
    }

    private static void reportFailure(Throwable failure, Path logDirectory) {
        YH_LOG_ERROR("The editor stopped because of an error.", failure);
        MessageBox.showError(
                Editor.applicationName,
                "The editor stopped because of an error:\n" + describe(failure) + "\n\nThe log is in " + logDirectory
                        + ".");
    }

    private static String describe(Throwable failure) {
        String message = failure.getMessage();
        return message != null ? message : failure.getClass().getSimpleName();
    }
}
