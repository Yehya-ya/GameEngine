package Panels;

import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.Renderer.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;

import java.io.File;
import java.util.Objects;

public class ContentBrowserPanel {
    private static final File assetDirectory = new File("assets");
    private static final float padding = 16.0f;
    private static final float thumbnailSize = 128.0f;
    private static final Texture directoryIcon = Texture.create("resources/Icons/ContentBrowser/DirectoryIcon.png");
    private static final Texture fileIcon = Texture.create("resources/Icons/ContentBrowser/FileIcon.png");

    private Scene scene;
    private File currentDirectory;

    public ContentBrowserPanel() {
        currentDirectory = new File("assets");
    }

    public ContentBrowserPanel(Scene scene) {
        this();
        this.scene = scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void onImGuiRender() {
        ImGui.begin("Content Browser");

        if (!currentDirectory.equals(assetDirectory)) {
            if (ImGui.button("<-")) {
                currentDirectory = currentDirectory.getParentFile();
            }
        }

        float cellSize = thumbnailSize + padding;

        float panelWidth = ImGui.getContentRegionAvailX();
        int columnCount = (int) (panelWidth / cellSize);
        if (columnCount < 1) columnCount = 1;

        ImGui.columns(columnCount);

        for (File file : Objects.requireNonNull(currentDirectory.listFiles())) {
            ImGui.pushID(file.getPath());
            Texture icon = file.isDirectory() ? directoryIcon : fileIcon;
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.imageButton(icon.getRendererId(), thumbnailSize, thumbnailSize, 0, 1, 1, 0);
            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload("CONTENT_BROWSER_ITEM", file);
                ImGui.endDragDropSource();
            }
            ImGui.popStyleColor();
            if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                if (file.isDirectory()) {
                    currentDirectory = file;
                }
            }
            ImGui.textWrapped(file.getName());

            ImGui.nextColumn();

            ImGui.popID();
        }

        ImGui.end();
    }
}
