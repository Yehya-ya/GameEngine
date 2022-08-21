package Utils;

import GameEngine.Engine.Utils.Action;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiWindowFlags;

import java.util.HashMap;

public class FileDialog {

    private static final HashMap<Long, Action> actions = new HashMap<>();

    public static void openFile(long id, String fileTypes, Action action) {
        ImGuiFileDialog.openModal("FileDialogKey", "Choose a file", fileTypes, "", null, 250, 1, id, ImGuiFileDialogFlags.HideColumnType | ImGuiFileDialogFlags.DisableCreateDirectoryButton);
        actions.put(id, action);
    }

    public static void saveFile(long id, String fileTypes, Action action) {
        saveFile(id, fileTypes, action, "untitled.yaml");
    }

    public static void saveFile(long id, String fileTypes, Action action, String fileName) {
        ImGuiFileDialog.openModal("FileDialogKey", "Save file as", fileTypes, fileName, null, 250, 1, id, ImGuiFileDialogFlags.HideColumnType | ImGuiFileDialogFlags.DisableCreateDirectoryButton | ImGuiFileDialogFlags.ConfirmOverwrite);
        actions.put(id, action);
    }


    public static void update() {
        if (ImGuiFileDialog.display("FileDialogKey", ImGuiWindowFlags.NoCollapse, 500, 400, 1000, 800)) {
            if (ImGuiFileDialog.isOk()) {
                long userDatas = ImGuiFileDialog.getUserDatas();
                actions.get(userDatas).action();
                actions.remove(userDatas);
            }
            ImGuiFileDialog.close();
        }
    }
}
