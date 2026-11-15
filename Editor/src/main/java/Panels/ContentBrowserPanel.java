package Panels;

import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.Renderer.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import java.io.File;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public class ContentBrowserPanel {
    public static final String WindowName = "Content Browser";
    private static final float padding = 16.0f;
    private static final float thumbnailSize = 128.0f;
    private final File assetDirectory;
    private final Texture directoryIcon;
    private final Texture fileIcon;

    private Scene scene;
    private File currentDirectory;
    private File[] currentFileList;

    public ContentBrowserPanel(@NotNull Path assetDirectory) {
        this.assetDirectory = assetDirectory.toFile();
        setCurrentDirectory(this.assetDirectory);
        directoryIcon = Texture.createFromResource("/Icons/ContentBrowser/DirectoryIcon.png");
        fileIcon = Texture.createFromResource("/Icons/ContentBrowser/FileIcon.png");
    }

    public void delete() {
        directoryIcon.delete();
        fileIcon.delete();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setCurrentDirectory(File directory) {
        currentDirectory = directory;
        File[] files = directory.listFiles();
        boolean isReadableDirectory = files != null;
        currentFileList = isReadableDirectory ? files : new File[0];
    }

    public void onImGuiRender() {
        ImGui.begin(WindowName);

        if (!currentDirectory.equals(assetDirectory)) {
            if (ImGui.button("<-")) {
                setCurrentDirectory(currentDirectory.getParentFile());
            }
        }

        float cellSize = thumbnailSize + padding;

        float panelWidth = ImGui.getContentRegionAvailX();
        int columnCount = (int) (panelWidth / cellSize);
        if (columnCount < 1) columnCount = 1;

        ImGui.columns(columnCount);

        for (File file : currentFileList) {
            ImGui.pushID(file.getPath());
            boolean isDirectory = file.isDirectory();
            Texture icon = isDirectory ? directoryIcon : fileIcon;
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.imageButton(icon.getRendererId(), thumbnailSize, thumbnailSize, 0, 1, 1, 0);
            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload("CONTENT_BROWSER_ITEM", file);
                ImGui.endDragDropSource();
            }
            ImGui.popStyleColor();
            if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                if (isDirectory) {
                    setCurrentDirectory(file);
                }
            }
            ImGui.textWrapped(file.getName());

            ImGui.nextColumn();

            ImGui.popID();
        }

        ImGui.end();
    }
}
