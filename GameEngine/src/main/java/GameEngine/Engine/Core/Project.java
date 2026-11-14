package GameEngine.Engine.Core;

import java.io.File;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public final class Project {
    private static final String assetFolderName = "assets";
    private final Path directory;

    public Project(@NotNull Path directory) {
        this.directory = directory.toAbsolutePath().normalize();
    }

    public @NotNull Path getDirectory() {
        return directory;
    }

    public @NotNull Path getAssetDirectory() {
        return directory.resolve(assetFolderName);
    }

    public @NotNull Path resolve(@NotNull String projectPath) {
        return directory.resolve(projectPath).normalize();
    }

    public @NotNull String toProjectPath(@NotNull Path file) {
        Path absoluteFile = file.toAbsolutePath().normalize();
        boolean isInsideProject = absoluteFile.startsWith(directory);
        if (!isInsideProject) {
            return absoluteFile.toString();
        }
        return directory.relativize(absoluteFile).toString().replace(File.separatorChar, '/');
    }
}
