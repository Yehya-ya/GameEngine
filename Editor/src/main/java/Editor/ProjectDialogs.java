package Editor;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.Core.ProjectSerializer;
import Utils.FileDialog;
import Utils.FileDialog.FileTypes;
import Utils.MessageBox;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ProjectDialogs {
    private static final FileTypes projectFileTypes = new FileTypes("Project files", List.of(Project.fileExtension));
    private static final String newProjectFolderTitle = "Choose or make an empty folder for the new project";
    private final FileDialog fileDialog = new FileDialog(projectsFolder());

    Optional<Project> newProject() {
        return fileDialog.selectFolder(newProjectFolderTitle).flatMap(ProjectDialogs::createProjectIn);
    }

    Optional<Project> openProject() {
        return fileDialog.openFile(projectFileTypes).flatMap(this::openProject);
    }

    Optional<Project> openProject(@NotNull Path projectFile) {
        return orShowError(ProjectSerializer.deserialize(projectFile), "Could not open " + projectFile + ".");
    }

    private static Optional<Project> createProjectIn(@NotNull Path folder) {
        List<Path> existingProjectFiles = ProjectSerializer.findProjectFiles(folder);
        if (!existingProjectFiles.isEmpty()) {
            MessageBox.showError(
                    Editor.applicationName,
                    folder + " is already a project.\nOpen "
                            + existingProjectFiles.getFirst().getFileName()
                            + " with Open Project instead.");
            return Optional.empty();
        }
        return orShowError(ProjectSerializer.create(folder), "Could not make a project in " + folder + ".");
    }

    private static Optional<Project> orShowError(@Nullable Project project, @NotNull String problem) {
        if (project == null) {
            MessageBox.showError(Editor.applicationName, problem + "\nThe console log says why.");
        }
        return Optional.ofNullable(project);
    }

    private static Path projectsFolder() {
        Path home = Path.of(System.getProperty("user.home"));
        Path documents = home.resolve("Documents");
        Path gameProjects = documents.resolve("GameProjects");
        if (Files.isDirectory(gameProjects)) {
            return gameProjects;
        }
        return Files.isDirectory(documents) ? documents : home;
    }
}
