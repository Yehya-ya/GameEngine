package GameEngine.Engine.Renderer;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class TextureLibrary {
    private static final Map<String, Texture> texturesByPath = new HashMap<>();

    private TextureLibrary() {}

    public static @NotNull Texture get(@NotNull String path) {
        return texturesByPath.computeIfAbsent(normalizedPath(path), key -> Texture.create(path));
    }

    public static void clear() {
        for (Texture texture : texturesByPath.values()) {
            texture.delete();
        }
        texturesByPath.clear();
        YH_LOG_TRACE("Cleared the texture library.");
    }

    private static String normalizedPath(String path) {
        try {
            return Path.of(path).toAbsolutePath().normalize().toString();
        } catch (InvalidPathException e) {
            return path;
        }
    }
}
