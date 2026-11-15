package Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class FileDialog {
    private final Path startDirectory;

    public FileDialog(@NotNull Path startDirectory) {
        this.startDirectory = startDirectory;
    }

    public void openFile(@NotNull FileTypes fileTypes, @NotNull Consumer<Path> onFileChosen) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            String chosenFile = TinyFileDialogs.tinyfd_openFileDialog(
                    "Choose a file", startPath(""), patterns(stack, fileTypes), fileTypes.description(), false);
            deliver(chosenFile, onFileChosen);
        }
    }

    public void saveFile(@NotNull FileTypes fileTypes, @NotNull String fileName, @NotNull Consumer<Path> onFileChosen) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            String chosenFile = TinyFileDialogs.tinyfd_saveFileDialog(
                    "Save file as", startPath(fileName), patterns(stack, fileTypes), fileTypes.description());
            deliver(chosenFile, onFileChosen);
        }
    }

    private static PointerBuffer patterns(MemoryStack stack, FileTypes fileTypes) {
        PointerBuffer patterns = stack.mallocPointer(fileTypes.extensions().size());
        fileTypes.extensions().forEach(extension -> patterns.put(stack.UTF8("*" + extension)));
        return patterns.flip();
    }

    private static void deliver(String chosenFile, Consumer<Path> onFileChosen) {
        if (chosenFile != null) {
            onFileChosen.accept(Path.of(chosenFile));
        }
    }

    private String startPath(String fileName) {
        Path directory = Files.isDirectory(startDirectory) ? startDirectory : Path.of("");
        return directory.toAbsolutePath() + File.separator + fileName;
    }

    public record FileTypes(
            @NotNull String description, @NotNull List<String> extensions) {}
}
