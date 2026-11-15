package Editor;

import Panels.ContentBrowserPanel;
import Panels.SceneHierarchyPanel;
import imgui.ImVec2;
import imgui.flag.ImGuiDir;
import imgui.internal.ImGui;
import imgui.internal.flag.ImGuiDockNodeFlags;
import imgui.type.ImInt;

public final class DockLayout {
    public static final int ToolbarNodeFlags =
            ImGuiDockNodeFlags.NoTabBar | ImGuiDockNodeFlags.NoResizeY | ImGuiDockNodeFlags.NoDockingOverMe;
    private static final float sideColumnWidthRatio = 0.2f;
    private static final float hierarchyHeightRatio = 0.4f;
    private static final float bottomRowHeightRatio = 0.3f;
    private static final float statsWidthRatio = 0.2f;
    private static final float toolbarHeight = 36.0f;

    private DockLayout() {}

    public static boolean exists(int dockspaceId) {
        return ImGui.dockBuilderGetNode(dockspaceId).isValidPtr();
    }

    public static void applyDefault(int dockspaceId, ImVec2 dockspaceSize) {
        ImGui.dockBuilderRemoveNode(dockspaceId);
        ImGui.dockBuilderAddNode(dockspaceId, ImGuiDockNodeFlags.DockSpace);
        ImGui.dockBuilderSetNodeSize(dockspaceId, dockspaceSize.x, dockspaceSize.y);

        ImInt sideColumn = new ImInt();
        ImInt mainArea = new ImInt();
        ImGui.dockBuilderSplitNode(dockspaceId, ImGuiDir.Left, sideColumnWidthRatio, sideColumn, mainArea);

        ImInt hierarchy = new ImInt();
        ImInt properties = new ImInt();
        ImGui.dockBuilderSplitNode(sideColumn.get(), ImGuiDir.Up, hierarchyHeightRatio, hierarchy, properties);

        ImInt bottomRow = new ImInt();
        ImInt viewportArea = new ImInt();
        ImGui.dockBuilderSplitNode(mainArea.get(), ImGuiDir.Down, bottomRowHeightRatio, bottomRow, viewportArea);

        ImInt stats = new ImInt();
        ImInt contentBrowser = new ImInt();
        ImGui.dockBuilderSplitNode(bottomRow.get(), ImGuiDir.Right, statsWidthRatio, stats, contentBrowser);

        ImInt toolbar = new ImInt();
        ImInt viewport = new ImInt();
        float toolbarHeightRatio = toolbarHeight / heightOf(viewportArea);
        ImGui.dockBuilderSplitNode(viewportArea.get(), ImGuiDir.Up, toolbarHeightRatio, toolbar, viewport);
        ImGui.dockBuilderSetNodeSize(toolbar.get(), widthOf(viewportArea), toolbarHeight);

        ImGui.dockBuilderDockWindow(SceneHierarchyPanel.HierarchyWindowName, hierarchy.get());
        ImGui.dockBuilderDockWindow(SceneHierarchyPanel.PropertiesWindowName, properties.get());
        ImGui.dockBuilderDockWindow(ContentBrowserPanel.WindowName, contentBrowser.get());
        ImGui.dockBuilderDockWindow(EditorLayer.StatsWindowName, stats.get());
        ImGui.dockBuilderDockWindow(EditorLayer.ToolbarWindowName, toolbar.get());
        ImGui.dockBuilderDockWindow(EditorLayer.ViewportWindowName, viewport.get());
        ImGui.dockBuilderFinish(dockspaceId);
    }

    private static float widthOf(ImInt nodeId) {
        return ImGui.dockBuilderGetNode(nodeId.get()).getSizeX();
    }

    private static float heightOf(ImInt nodeId) {
        return ImGui.dockBuilderGetNode(nodeId.get()).getSizeY();
    }
}
