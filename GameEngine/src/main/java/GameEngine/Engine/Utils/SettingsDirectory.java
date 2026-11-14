package GameEngine.Engine.Utils;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public final class SettingsDirectory {
    private static final String folderName = "GameEngine";

    private SettingsDirectory() {}

    public static @NotNull Path file(@NotNull String applicationName, @NotNull String fileName) {
        Path directory = location().resolve(applicationName);
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            YH_LOG_ERROR("Could not create the settings folder `{}`: {}", directory, e.getMessage());
        }
        return directory.resolve(fileName);
    }

    private static @NotNull Path location() {
        String windowsAppData = System.getenv("APPDATA");
        if (windowsAppData != null) {
            return Path.of(windowsAppData, folderName);
        }
        String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
        if (xdgConfigHome != null) {
            return Path.of(xdgConfigHome, folderName);
        }
        return Path.of(System.getProperty("user.home"), ".config", folderName);
    }
}
