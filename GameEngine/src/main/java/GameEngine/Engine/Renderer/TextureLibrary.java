package GameEngine.Engine.Renderer;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class TextureLibrary {
    private final Map<Path, Texture> texturesByFile = new HashMap<>();

    public @NotNull Texture get(@NotNull Path file) {
        Path absoluteFile = file.toAbsolutePath().normalize();
        return texturesByFile.computeIfAbsent(absoluteFile, Texture::create);
    }

    public void delete() {
        for (Texture texture : texturesByFile.values()) {
            texture.delete();
        }
        texturesByFile.clear();
    }
}
