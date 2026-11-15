package Editor;

import GameEngine.Engine.Utils.Resources;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;

final class BuildInfo {
    private static final String resourceName = "/build-info.properties";

    private BuildInfo() {}

    static @NotNull String version() {
        Properties buildInfo = new Properties();
        try {
            buildInfo.load(new StringReader(Resources.readText(resourceName)));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + resourceName, e);
        }
        return buildInfo.getProperty("version");
    }
}
