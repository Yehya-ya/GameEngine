package GameEngine.Engine.Renderer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TextureTest {
    @TempDir
    Path directory;

    @Test
    void pngFileIsAnImage() throws IOException {
        assertTrue(Texture.isImageFile(Files.createFile(directory.resolve("dirt.png"))));
    }

    @Test
    void imageExtensionIgnoresLetterCase() throws IOException {
        assertTrue(Texture.isImageFile(Files.createFile(directory.resolve("DIRT.PNG"))));
    }

    @Test
    void everyImageExtensionIsAccepted() throws IOException {
        for (String extension : Texture.ImageExtensions) {
            assertTrue(Texture.isImageFile(Files.createFile(directory.resolve("image" + extension))), extension);
        }
    }

    @Test
    void sceneFileIsNotAnImage() throws IOException {
        assertFalse(Texture.isImageFile(Files.createFile(directory.resolve("demo.yaml"))));
    }

    @Test
    void folderNamedLikeAnImageIsNotAnImage() throws IOException {
        assertFalse(Texture.isImageFile(Files.createDirectory(directory.resolve("textures.png"))));
    }

    @Test
    void missingFileIsNotAnImage() {
        assertFalse(Texture.isImageFile(directory.resolve("missing.png")));
    }
}
