package GameEngine.Engine.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;

public final class Resources {

    private Resources() {}

    public static @NotNull String readText(@NotNull String resourceName) {
        return new String(readBytes(resourceName), StandardCharsets.UTF_8);
    }

    public static byte @NotNull [] readBytes(@NotNull String resourceName) {
        try (InputStream stream = Resources.class.getResourceAsStream(resourceName)) {
            if (stream == null) {
                throw new IllegalArgumentException("Resource not found on the classpath: " + resourceName);
            }
            return stream.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read the resource " + resourceName, e);
        }
    }
}
