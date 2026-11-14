package GameEngine.Engine.Renderer;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class TextureLibrary {
    private static final Map<Path, Texture> texturesByFile = new HashMap<>();

    private TextureLibrary() {}

    public static @NotNull Texture get(@NotNull Path file) {
        Path absoluteFile = file.toAbsolutePath().normalize();
        return texturesByFile.computeIfAbsent(absoluteFile, key -> Texture.create(key.toString()));
    }

    public static void clear() {
        for (Texture texture : texturesByFile.values()) {
            texture.delete();
        }
        texturesByFile.clear();
        YH_LOG_TRACE("Cleared the texture library.");
    }
}
