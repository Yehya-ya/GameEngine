package GameEngine.Engine.Core;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;

public final class ProjectSerializer {
    private static final String startSceneKey = "startScene";
    private static final List<String> assetFolders = List.of("scenes", "textures");

    private ProjectSerializer() {}

    public static @Nullable Project create(@NotNull Path directory) {
        List<Path> existingProjectFiles = findProjectFiles(directory);
        if (!existingProjectFiles.isEmpty()) {
            YH_LOG_ERROR(
                    "`{}` is already a project, with the project file `{}`.",
                    directory,
                    existingProjectFiles.getFirst().getFileName());
            return null;
        }

        Project project = new Project(directory);
        try {
            for (String assetFolder : assetFolders) {
                Files.createDirectories(project.getAssetDirectory().resolve(assetFolder));
            }
        } catch (IOException e) {
            YH_LOG_ERROR("Could not create the project folders in `{}`: {}", project.getDirectory(), e.getMessage());
            return null;
        }
        return serialize(project) ? project : null;
    }

    public static boolean serialize(@NotNull Project project) {
        Map<String, Object> projectData = new LinkedHashMap<>();
        projectData.put(startSceneKey, project.getStartScene());

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        try {
            Files.writeString(project.getFile(), new Yaml(options).dump(projectData));
        } catch (IOException e) {
            YH_LOG_ERROR("Could not save the project file `{}`: {}", project.getFile(), e.getMessage());
            return false;
        }
        YH_LOG_TRACE("Saved the project file `{}`.", project.getFile());
        return true;
    }

    public static @Nullable Project deserialize(@NotNull Path file) {
        if (!hasProjectExtension(file)) {
            YH_LOG_ERROR("`{}` is not a project file. Project files end in `{}`.", file, Project.fileExtension);
            return null;
        }

        Object projectData;
        try (Reader reader = Files.newBufferedReader(file)) {
            projectData = new Yaml(new SafeConstructor(new LoaderOptions())).load(reader);
        } catch (NoSuchFileException e) {
            YH_LOG_ERROR("The project file `{}` does not exist.", file);
            return null;
        } catch (IOException | YAMLException e) {
            YH_LOG_ERROR("Could not read the project file `{}`: {}", file, e.getMessage());
            return null;
        }

        boolean isEmptyFile = projectData == null;
        if (!isEmptyFile && !(projectData instanceof Map<?, ?>)) {
            YH_LOG_ERROR("The project file `{}` does not hold project settings.", file);
            return null;
        }
        Map<?, ?> settings = isEmptyFile ? Map.of() : (Map<?, ?>) projectData;

        Object startScene = settings.get(startSceneKey);
        if (startScene != null && !(startScene instanceof String)) {
            YH_LOG_ERROR("The start scene in `{}` is not a path: {}", file, startScene);
            return null;
        }

        Path absoluteFile = file.toAbsolutePath().normalize();
        return new Project(absoluteFile.getParent(), projectNameOf(absoluteFile), (String) startScene);
    }

    public static @NotNull List<Path> findProjectFiles(@NotNull Path directory) {
        if (!Files.isDirectory(directory)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(ProjectSerializer::hasProjectExtension)
                    .filter(Files::isRegularFile)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            YH_LOG_ERROR("Could not look for project files in `{}`: {}", directory, e.getMessage());
            return List.of();
        }
    }

    private static boolean hasProjectExtension(@NotNull Path file) {
        return file.getFileName().toString().endsWith(Project.fileExtension);
    }

    private static @NotNull String projectNameOf(@NotNull Path file) {
        String fileName = file.getFileName().toString();
        return fileName.substring(0, fileName.length() - Project.fileExtension.length());
    }
}
