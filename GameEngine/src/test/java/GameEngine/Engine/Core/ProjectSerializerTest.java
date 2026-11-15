package GameEngine.Engine.Core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProjectSerializerTest {
    @TempDir
    Path projectDirectory;

    @Test
    void savedProjectLoadsBackWithItsNameFolderAndStartScene() {
        Project original = new Project(projectDirectory, "MyGame", "assets/scenes/level1.yaml");

        Project loaded = saveAndLoad(original);

        assertEquals("MyGame", loaded.getName());
        assertEquals(original.getDirectory(), loaded.getDirectory());
        assertEquals(original.getFile(), loaded.getFile());
        assertEquals("assets/scenes/level1.yaml", loaded.getStartScene());
    }

    @Test
    void projectWithoutStartSceneLoadsBackWithoutOne() {
        Project loaded = saveAndLoad(new Project(projectDirectory, "MyGame", null));

        assertNull(loaded.getStartScene());
    }

    @Test
    void projectFileIsPlainYaml() throws IOException {
        Project project = new Project(projectDirectory, "MyGame", "assets/scenes/level1.yaml");

        ProjectSerializer.serialize(project);

        assertEquals("startScene: assets/scenes/level1.yaml\n", Files.readString(project.getFile()));
    }

    @Test
    void emptyProjectFileOpensWithoutStartScene() throws IOException {
        Project project = load("MyGame.geproj", "");

        assertNotNull(project);
        assertEquals("MyGame", project.getName());
        assertNull(project.getStartScene());
    }

    @Test
    void fileWithoutTheProjectExtensionIsNotOpened() throws IOException {
        assertNull(load("level1.yaml", "startScene: null\n"));
    }

    @Test
    void projectFileWithoutSettingsIsNotOpened() throws IOException {
        assertNull(load("MyGame.geproj", "- first\n- second\n"));
    }

    @Test
    void startSceneThatIsNotAPathIsNotOpened() throws IOException {
        assertNull(load("MyGame.geproj", "startScene: [first, second]\n"));
    }

    @Test
    void sceneFileIsNotOpenedAsAProject() throws IOException {
        assertNull(load("MyGame.geproj", "!Scene\ntitle: Level 1\n"));
    }

    @Test
    void missingProjectFileIsNotOpened() {
        assertNull(ProjectSerializer.deserialize(projectDirectory.resolve("MyGame.geproj")));
    }

    @Test
    void createMakesTheProjectFileAndAssetFolders() {
        Path directory = projectDirectory.resolve("MyGame");

        Project project = ProjectSerializer.create(directory);

        assertNotNull(project);
        assertEquals("MyGame", project.getName());
        assertTrue(Files.isRegularFile(directory.resolve("MyGame.geproj")));
        assertTrue(Files.isDirectory(directory.resolve("assets").resolve("scenes")));
        assertTrue(Files.isDirectory(directory.resolve("assets").resolve("textures")));
    }

    @Test
    void createKeepsTheAssetsAFolderAlreadyHas() throws IOException {
        Path texture = projectDirectory.resolve("assets").resolve("textures").resolve("dirt.png");
        Files.createDirectories(texture.getParent());
        Files.write(texture, new byte[] {1, 2, 3});

        assertNotNull(ProjectSerializer.create(projectDirectory));

        assertArrayEquals(new byte[] {1, 2, 3}, Files.readAllBytes(texture));
    }

    @Test
    void createRefusesAFolderThatIsAlreadyAProject() throws IOException {
        Files.writeString(projectDirectory.resolve("Other.geproj"), "");

        assertNull(ProjectSerializer.create(projectDirectory));
    }

    @Test
    void findsOnlyTheProjectFilesInAFolderInNameOrder() throws IOException {
        Files.writeString(projectDirectory.resolve("Second.geproj"), "");
        Files.writeString(projectDirectory.resolve("First.geproj"), "");
        Files.writeString(projectDirectory.resolve("notes.txt"), "");
        Files.createDirectories(projectDirectory.resolve("Folder.geproj"));

        List<Path> projectFiles = ProjectSerializer.findProjectFiles(projectDirectory);

        assertEquals(
                List.of(projectDirectory.resolve("First.geproj"), projectDirectory.resolve("Second.geproj")),
                projectFiles);
    }

    @Test
    void folderWithoutAProjectFileHasNone() {
        assertEquals(List.of(), ProjectSerializer.findProjectFiles(projectDirectory));
    }

    @Test
    void missingFolderHasNoProjectFiles() {
        assertEquals(List.of(), ProjectSerializer.findProjectFiles(projectDirectory.resolve("missing")));
    }

    private Project saveAndLoad(Project project) {
        assertTrue(ProjectSerializer.serialize(project));
        Project loaded = ProjectSerializer.deserialize(project.getFile());
        assertNotNull(loaded);
        return loaded;
    }

    private Project load(String fileName, String fileContent) throws IOException {
        Path file = Files.writeString(projectDirectory.resolve(fileName), fileContent);
        return ProjectSerializer.deserialize(file);
    }
}
