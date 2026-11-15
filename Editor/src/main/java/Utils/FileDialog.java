package Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class FileDialog {
    private final Path startDirectory;

    public FileDialog(@NotNull Path startDirectory) {
        this.startDirectory = startDirectory;
    }

    public Optional<Path> openFile(@NotNull FileTypes fileTypes) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return toPath(TinyFileDialogs.tinyfd_openFileDialog(
                    "Choose a file", startPath(""), patterns(stack, fileTypes), fileTypes.description(), false));
        }
    }

    public Optional<Path> saveFile(@NotNull FileTypes fileTypes, @NotNull String fileName) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return toPath(TinyFileDialogs.tinyfd_saveFileDialog(
                    "Save file as", startPath(fileName), patterns(stack, fileTypes), fileTypes.description()));
        }
    }

    public Optional<Path> selectFolder(@NotNull String title) {
        return toPath(TinyFileDialogs.tinyfd_selectFolderDialog(title, startPath("")));
    }

    private static PointerBuffer patterns(MemoryStack stack, FileTypes fileTypes) {
        PointerBuffer patterns = stack.mallocPointer(fileTypes.extensions().size());
        fileTypes.extensions().forEach(extension -> patterns.put(stack.UTF8("*" + extension)));
        return patterns.flip();
    }

    private static Optional<Path> toPath(String chosenFile) {
        return Optional.ofNullable(chosenFile).map(Path::of);
    }

    private String startPath(String fileName) {
        Path directory = Files.isDirectory(startDirectory) ? startDirectory : Path.of("");
        return directory.toAbsolutePath() + File.separator + fileName;
    }

    public record FileTypes(
            @NotNull String description, @NotNull List<String> extensions) {}
}
