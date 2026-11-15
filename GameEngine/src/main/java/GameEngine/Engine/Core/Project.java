package GameEngine.Engine.Core;

import java.io.File;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Project {
    public static final String fileExtension = ".geproj";
    private static final String assetFolderName = "assets";
    private static final String driveRootProjectName = "Project";
    private final Path directory;
    private final String name;
    private final @Nullable String startScene;

    public Project(@NotNull Path directory) {
        this(directory, folderNameOf(directory), null);
    }

    public Project(@NotNull Path directory, @NotNull String name, @Nullable String startScene) {
        this.directory = directory.toAbsolutePath().normalize();
        this.name = name;
        this.startScene = startScene;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Path getFile() {
        return directory.resolve(name + fileExtension);
    }

    public @Nullable String getStartScene() {
        return startScene;
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

    private static @NotNull String folderNameOf(@NotNull Path directory) {
        Path folderName = directory.toAbsolutePath().normalize().getFileName();
        boolean isDriveRoot = folderName == null;
        return isDriveRoot ? driveRootProjectName : folderName.toString();
    }
}
