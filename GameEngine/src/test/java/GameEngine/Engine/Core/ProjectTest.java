package GameEngine.Engine.Core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProjectTest {
    @TempDir
    Path projectDirectory;

    @TempDir
    Path otherDirectory;

    @Test
    void fileInsideTheProjectBecomesARelativePathWithForwardSlashes() {
        Project project = new Project(projectDirectory);
        Path texture = projectDirectory.resolve("assets").resolve("textures").resolve("dirt.png");

        assertEquals("assets/textures/dirt.png", project.toProjectPath(texture));
    }

    @Test
    void projectPathResolvesBackToTheFile() {
        Project project = new Project(projectDirectory);
        Path texture = projectDirectory.resolve("assets").resolve("textures").resolve("dirt.png");

        assertEquals(texture.toAbsolutePath().normalize(), project.resolve(project.toProjectPath(texture)));
    }

    @Test
    void fileOutsideTheProjectKeepsItsFullPath() {
        Project project = new Project(projectDirectory);
        Path texture = otherDirectory.resolve("dirt.png");

        String projectPath = project.toProjectPath(texture);

        assertEquals(texture.toAbsolutePath().normalize().toString(), projectPath);
        assertEquals(texture.toAbsolutePath().normalize(), project.resolve(projectPath));
    }

    @Test
    void assetsLiveInTheProjectsAssetsFolder() {
        Project project = new Project(projectDirectory);

        assertEquals(projectDirectory.resolve("assets").toAbsolutePath().normalize(), project.getAssetDirectory());
    }
}
