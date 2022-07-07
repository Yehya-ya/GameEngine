package Panels;

import GameEngine.Engine.ECS.Scene;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.jetbrains.annotations.NotNull;

public class SceneHierarchyPanel {
    private Scene scene;
    private Entity selectedEntity;


    public SceneHierarchyPanel(Scene scene) {
        setScene(scene);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void onImGuiRender() {
        ImGui.begin("Scene Hierarchy");

        TagManager tagManager = scene.getTagManager();
        for (String tag : tagManager.getRegisteredTags()) {
            drawEntityNode(tagManager.getEntity(tag), tag);
        }

        ImGui.end();
    }

    private void drawEntityNode(@NotNull Entity e, String tag) {
        int flags = ImGuiTreeNodeFlags.OpenOnArrow | (e == selectedEntity ? ImGuiTreeNodeFlags.Selected : 0);
        boolean opened = ImGui.treeNodeEx(e.getId(), flags, tag);
        if (ImGui.isItemClicked()) {
            selectedEntity = e;
        }
        if (opened) {
            ImGui.treePop();
        }
    }
}
