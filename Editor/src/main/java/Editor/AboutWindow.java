package Editor;

import GameEngine.Engine.Core.BuildType;
import GameEngine.Engine.Utils.LogFiles;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

final class AboutWindow {
    private static final String popupName = "About the Editor";
    private static final float centerPivot = 0.5f;
    private final String versionLine;
    private final String logDirectory;
    private boolean isOpenRequested;

    AboutWindow() {
        String buildType = BuildType.current().name().toLowerCase();
        versionLine = "Version " + BuildInfo.version() + ", " + buildType + " build";
        logDirectory = System.getProperty(LogFiles.directoryProperty, "not set");
    }

    void open() {
        isOpenRequested = true;
    }

    void draw() {
        if (isOpenRequested) {
            ImGui.openPopup(popupName);
            isOpenRequested = false;
        }

        ImGuiViewport viewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(
                viewport.getCenterX(), viewport.getCenterY(), ImGuiCond.Appearing, centerPivot, centerPivot);
        if (!ImGui.beginPopupModal(popupName, ImGuiWindowFlags.AlwaysAutoResize)) {
            return;
        }

        ImGui.text("GameEngine Editor");
        ImGui.text(versionLine);
        ImGui.separator();
        ImGui.text("Licensed under the Apache License 2.0.");
        ImGui.text("THIRD-PARTY-NOTICES.txt lists the third-party software it uses and their licenses.");
        ImGui.separator();
        ImGui.text("Log folder:");
        ImGui.textDisabled(logDirectory);
        if (ImGui.button("Copy Log Folder")) {
            ImGui.setClipboardText(logDirectory);
        }
        ImGui.sameLine();
        if (ImGui.button("Close")) {
            ImGui.closeCurrentPopup();
        }
        ImGui.endPopup();
    }
}
