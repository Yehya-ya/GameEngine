package Utils;

import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiWindowFlags;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class FileDialog {
    private static final String dialogKey = "FileDialog";
    private static final int openFlags =
            ImGuiFileDialogFlags.HideColumnType | ImGuiFileDialogFlags.DisableCreateDirectoryButton;
    private static final int saveFlags = openFlags | ImGuiFileDialogFlags.ConfirmOverwrite;
    private static final int singleFile = 1;
    private static final long noUserData = 0;

    private final Path startDirectory;
    private Consumer<Path> onFileChosen;

    public FileDialog(@NotNull Path startDirectory) {
        this.startDirectory = startDirectory;
    }

    public void openFile(String fileTypes, Consumer<Path> onFileChosen) {
        show("Choose a file", fileTypes, "", openFlags, onFileChosen);
    }

    public void saveFile(String fileTypes, String fileName, Consumer<Path> onFileChosen) {
        show("Save file as", fileTypes, fileName, saveFlags, onFileChosen);
    }

    public void onImGuiRender() {
        boolean isFinished = ImGuiFileDialog.display(dialogKey, ImGuiWindowFlags.NoCollapse, 500, 400, 1000, 800);
        if (!isFinished) {
            return;
        }
        Consumer<Path> callback = onFileChosen;
        Path chosenFile = ImGuiFileDialog.isOk() ? Path.of(ImGuiFileDialog.getFilePathName()) : null;
        onFileChosen = null;
        ImGuiFileDialog.close();
        if (chosenFile != null) {
            callback.accept(chosenFile);
        }
    }

    private void show(String title, String fileTypes, String fileName, int flags, Consumer<Path> onFileChosen) {
        if (ImGuiFileDialog.isOpened(dialogKey)) {
            return;
        }
        this.onFileChosen = onFileChosen;
        ImGuiFileDialog.openModal(dialogKey, title, fileTypes, startPath(), fileName, singleFile, noUserData, flags);
    }

    private String startPath() {
        boolean startDirectoryExists = Files.isDirectory(startDirectory);
        return startDirectoryExists ? startDirectory.toString() : "";
    }
}
