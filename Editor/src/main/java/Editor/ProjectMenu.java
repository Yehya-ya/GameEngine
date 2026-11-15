package Editor;

import GameEngine.Engine.Core.Project;
import imgui.ImGui;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;

final class ProjectMenu {
    private final Project openProject;
    private final ProjectSwitcher projects;
    private final ProjectDialogs projectDialogs;

    ProjectMenu(@NotNull Project openProject, @NotNull ProjectSwitcher projects) {
        this.openProject = openProject;
        this.projects = projects;
        projectDialogs = new ProjectDialogs();
    }

    void drawItems() {
        if (ImGui.menuItem("New Project...")) {
            projectDialogs.newProject().ifPresent(projects::openProject);
        }
        if (ImGui.menuItem("Open Project...")) {
            projectDialogs.openProject().ifPresent(projects::openProject);
        }
        drawRecentProjects();
        if (ImGui.menuItem("Close Project")) {
            projects.closeProject();
        }
    }

    private void drawRecentProjects() {
        List<Path> otherProjectFiles = projects.recentProjectFiles().stream()
                .filter(projectFile -> !projectFile.equals(openProject.getFile()))
                .toList();
        if (ImGui.beginMenu("Recent Projects", !otherProjectFiles.isEmpty())) {
            for (Path projectFile : otherProjectFiles) {
                String uniqueLabel = Project.nameFromFile(projectFile) + "##" + projectFile;
                if (ImGui.menuItem(uniqueLabel, projectFile.getParent().toString())) {
                    projectDialogs.openProject(projectFile).ifPresent(projects::openProject);
                }
            }
            ImGui.endMenu();
        }
    }
}
