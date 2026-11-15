package Editor;

import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Core.Project;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class ProjectPickerLayer extends Layer {
    private static final String windowName = "Projects";
    private static final float windowPadding = 32.0f;
    private static final int windowFlags = ImGuiWindowFlags.NoTitleBar
            | ImGuiWindowFlags.NoResize
            | ImGuiWindowFlags.NoMove
            | ImGuiWindowFlags.NoCollapse
            | ImGuiWindowFlags.NoDocking
            | ImGuiWindowFlags.NoSavedSettings
            | ImGuiWindowFlags.NoBringToFrontOnFocus;
    private final RecentProjects recentProjects;
    private final Consumer<Project> openProject;
    private final ProjectDialogs projectDialogs;

    public ProjectPickerLayer(@NotNull RecentProjects recentProjects, @NotNull Consumer<Project> openProject) {
        super("ProjectPickerLayer");
        this.recentProjects = recentProjects;
        this.openProject = openProject;
        projectDialogs = new ProjectDialogs();
    }

    @Override
    public void onImGuiRender() {
        ImGuiViewport viewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(viewport.getPosX(), viewport.getPosY());
        ImGui.setNextWindowSize(viewport.getSizeX(), viewport.getSizeY());
        ImGui.setNextWindowViewport(viewport.getID());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, windowPadding, windowPadding);
        ImGui.begin(windowName, windowFlags);
        ImGui.popStyleVar();

        drawProjectButtons();
        ImGui.spacing();
        ImGui.separator();
        ImGui.spacing();
        drawRecentProjects();

        ImGui.end();
    }

    private void drawProjectButtons() {
        if (ImGui.button("New Project...")) {
            projectDialogs.newProject().ifPresent(openProject);
        }
        ImGui.sameLine();
        if (ImGui.button("Open Project...")) {
            projectDialogs.openProject().ifPresent(openProject);
        }
    }

    private void drawRecentProjects() {
        ImGui.text("Recent projects");
        List<Path> projectFiles = recentProjects.getProjectFiles();
        if (projectFiles.isEmpty()) {
            ImGui.textDisabled("No recent projects yet.");
            return;
        }
        if (ImGui.beginTable("RecentProjects", 2, ImGuiTableFlags.RowBg)) {
            for (Path projectFile : projectFiles) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                String uniqueLabel = Project.nameFromFile(projectFile) + "##" + projectFile;
                if (ImGui.selectable(uniqueLabel, false, ImGuiSelectableFlags.SpanAllColumns)) {
                    projectDialogs.openProject(projectFile).ifPresent(openProject);
                }
                ImGui.tableNextColumn();
                ImGui.textDisabled(projectFile.getParent().toString());
            }
            ImGui.endTable();
        }
    }
}
