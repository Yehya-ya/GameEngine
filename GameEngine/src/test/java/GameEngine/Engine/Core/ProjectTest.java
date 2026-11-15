package GameEngine.Engine.Core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    void projectInAFolderIsNamedAfterItAndHasNoStartScene() {
        Project project = new Project(projectDirectory);
        String folderName = projectDirectory.getFileName().toString();

        assertEquals(folderName, project.getName());
        assertEquals(
                projectDirectory
                        .resolve(folderName + ".geproj")
                        .toAbsolutePath()
                        .normalize(),
                project.getFile());
        assertNull(project.getStartScene());
    }

    @Test
    void projectFileIsNamedAfterTheProject() {
        Project project = new Project(projectDirectory, "MyGame", "assets/scenes/level1.yaml");

        assertEquals(projectDirectory.resolve("MyGame.geproj").toAbsolutePath().normalize(), project.getFile());
        assertEquals("assets/scenes/level1.yaml", project.getStartScene());
    }
}
