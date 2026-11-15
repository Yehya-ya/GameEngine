package Editor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RecentProjectsTest {
    @TempDir
    Path settingsDirectory;

    @TempDir
    Path projectsDirectory;

    @Test
    void noStoredListMeansNoRecentProjects() {
        assertEquals(List.of(), recentProjects().getProjectFiles());
    }

    @Test
    void newestProjectComesFirst() throws IOException {
        Path first = projectFile("First");
        Path second = projectFile("Second");
        RecentProjects recentProjects = recentProjects();

        recentProjects.add(first);
        recentProjects.add(second);

        assertEquals(List.of(second, first), recentProjects.getProjectFiles());
    }

    @Test
    void openingAProjectAgainMovesItToTheFrontOnce() throws IOException {
        Path first = projectFile("First");
        Path second = projectFile("Second");
        RecentProjects recentProjects = recentProjects();

        recentProjects.add(first);
        recentProjects.add(second);
        recentProjects.add(first.getParent().resolve(".").resolve(first.getFileName()));

        assertEquals(List.of(first, second), recentProjects.getProjectFiles());
    }

    @Test
    void keepsTheTenNewestProjects() throws IOException {
        List<Path> projectFiles = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            projectFiles.add(projectFile("Project" + i));
        }
        RecentProjects recentProjects = recentProjects();

        projectFiles.forEach(recentProjects::add);

        assertEquals(projectFiles.subList(2, 12).reversed(), recentProjects.getProjectFiles());
    }

    @Test
    void listIsRememberedForTheNextStart() throws IOException {
        Path first = projectFile("First");
        Path second = projectFile("Second");
        RecentProjects recentProjects = recentProjects();
        recentProjects.add(first);
        recentProjects.add(second);

        assertEquals(List.of(second, first), recentProjects().getProjectFiles());
    }

    @Test
    void projectsWhoseFileIsGoneAreDropped() throws IOException {
        Path first = projectFile("First");
        Path second = projectFile("Second");
        RecentProjects recentProjects = recentProjects();
        recentProjects.add(first);
        recentProjects.add(second);

        Files.delete(first);

        assertEquals(List.of(second), recentProjects().getProjectFiles());
    }

    private RecentProjects recentProjects() {
        return RecentProjects.load(settingsDirectory.resolve(RecentProjects.fileName));
    }

    private Path projectFile(String name) throws IOException {
        Path file = projectsDirectory.resolve(name).resolve(name + ".geproj");
        Files.createDirectories(file.getParent());
        return Files.writeString(file, "").toAbsolutePath().normalize();
    }
}
