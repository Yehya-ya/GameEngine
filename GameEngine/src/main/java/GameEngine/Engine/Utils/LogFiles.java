package GameEngine.Engine.Utils;

import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public final class LogFiles {
    public static final String directoryProperty = "gameengine.logDirectory";

    private LogFiles() {}

    public static void writeTo(@NotNull Path directory) {
        System.setProperty(directoryProperty, directory.toAbsolutePath().toString());
    }
}
