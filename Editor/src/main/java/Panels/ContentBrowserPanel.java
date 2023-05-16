package Panels;

import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.Renderer.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;

import java.io.File;

public class ContentBrowserPanel {
    private static final File assetDirectory = new File("assets");
    private static final float padding = 16.0f;
    private static final float thumbnailSize = 128.0f;
    private Texture directoryIcon;
    private Texture fileIcon;

    private Scene scene;
    private File currentDirectory;
    private File[] currentFileList;

    public ContentBrowserPanel() {
        setCurrentDirectory(new File("assets"));
        directoryIcon = Texture.create("resources/Icons/ContentBrowser/DirectoryIcon.png");
        fileIcon = Texture.create("resources/Icons/ContentBrowser/FileIcon.png");
    }

    public ContentBrowserPanel(Scene scene) {
        this();
        this.scene = scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setCurrentDirectory(File file) {
        currentDirectory = file;
        currentFileList = file.listFiles();
    }

    public void onImGuiRender() {
        ImGui.begin("Content Browser");

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
