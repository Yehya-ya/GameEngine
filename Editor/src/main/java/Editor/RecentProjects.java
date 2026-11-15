package Editor;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class RecentProjects {
    public static final String fileName = "recent-projects.txt";
    private static final int maxCount = 10;
    private final Path storageFile;
    private final List<Path> projectFiles;

    private RecentProjects(@NotNull Path storageFile, @NotNull List<Path> projectFiles) {
        this.storageFile = storageFile;
        this.projectFiles = projectFiles;
    }

    public static @NotNull RecentProjects load(@NotNull Path storageFile) {
        List<Path> existingProjectFiles = readProjectFiles(storageFile).stream()
                .map(RecentProjects::normalized)
                .filter(Files::isRegularFile)
                .distinct()
                .limit(maxCount)
                .toList();
        return new RecentProjects(storageFile, new ArrayList<>(existingProjectFiles));
    }

    public @NotNull List<Path> getProjectFiles() {
        return List.copyOf(projectFiles);
    }

    public void add(@NotNull Path projectFile) {
        Path normalizedFile = normalized(projectFile);
        projectFiles.remove(normalizedFile);
        projectFiles.addFirst(normalizedFile);
        while (projectFiles.size() > maxCount) {
            projectFiles.removeLast();
        }
        save();
    }

    private void save() {
        try {
            Files.write(storageFile, projectFiles.stream().map(Path::toString).toList());
        } catch (IOException e) {
            YH_LOG_ERROR("Could not save the recent projects to `{}`: {}", storageFile, e.getMessage());
        }
    }

    private static @NotNull List<Path> readProjectFiles(@NotNull Path storageFile) {
        if (!Files.isRegularFile(storageFile)) {
            return List.of();
        }
        try {
            return Files.readAllLines(storageFile).stream()
                    .filter(line -> !line.isBlank())
                    .map(Path::of)
                    .toList();
        } catch (IOException | InvalidPathException e) {
            YH_LOG_ERROR("Could not read the recent projects from `{}`: {}", storageFile, e.getMessage());
            return List.of();
        }
    }

    private static @NotNull Path normalized(@NotNull Path file) {
        return file.toAbsolutePath().normalize();
    }
}
