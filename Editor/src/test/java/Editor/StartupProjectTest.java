package Editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StartupProjectTest {
    private static final String[] noArguments = {};

    @TempDir
    Path projectDirectory;

    @Test
    void givenProjectFileIsOpened() throws IOException {
        Path projectFile = projectFile("MyGame");

        assertEquals(projectFile, StartupProject.choose(arguments(projectFile), List.of()));
    }

    @Test
    void givenFolderOpensItsProjectFile() throws IOException {
        Path projectFile = projectFile("MyGame");

        assertEquals(projectFile, StartupProject.choose(arguments(projectDirectory), List.of()));
    }

    @Test
    void givenFolderWithoutAProjectFileOpensNothing() {
        assertNull(StartupProject.choose(arguments(projectDirectory), List.of()));
    }

    @Test
    void givenFolderWithSeveralProjectFilesOpensNothing() throws IOException {
        projectFile("First");
        projectFile("Second");

        assertNull(StartupProject.choose(arguments(projectDirectory), List.of()));
    }

    @Test
    void givenPathThatDoesNotExistOpensNothing() {
        Path missingFolder = projectDirectory.resolve("Missing");

        assertNull(StartupProject.choose(arguments(missingFolder), List.of()));
    }

    @Test
    void givenProjectWinsOverTheRecentOnes() throws IOException {
        Path projectFile = projectFile("MyGame");
        Path recentProjectFile = projectDirectory.resolve("Recent").resolve("Recent.geproj");

        assertEquals(projectFile, StartupProject.choose(arguments(projectFile), List.of(recentProjectFile)));
    }

    @Test
    void withoutArgumentTheLastProjectIsOpened() {
        Path last = projectDirectory.resolve("Last.geproj");
        Path earlier = projectDirectory.resolve("Earlier.geproj");

        assertEquals(last, StartupProject.choose(noArguments, List.of(last, earlier)));
    }

    @Test
    void withoutArgumentOrRecentProjectsNothingIsOpened() {
        assertNull(StartupProject.choose(noArguments, List.of()));
    }

    private Path projectFile(String name) throws IOException {
        return Files.writeString(projectDirectory.resolve(name + ".geproj"), "")
                .toAbsolutePath()
                .normalize();
    }

    private static String[] arguments(Path projectPath) {
        return new String[] {projectPath.toString()};
    }
}
