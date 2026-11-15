package Editor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Entity;
import GameEngine.Engine.ECS.Scene;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SceneChangesTest {
    @TempDir
    Path projectDirectory;

    @Test
    void sceneJustSavedOrOpenedHasNoUnsavedChanges() {
        Scene scene = new Scene("Level", null);
        scene.createEntity("Player");
        SceneChanges sceneChanges = new SceneChanges(new Project(projectDirectory));

        sceneChanges.markSaved(scene);

        assertFalse(sceneChanges.hasUnsavedChanges(scene));
    }

    @Test
    void movingAnEntityIsAnUnsavedChange() {
        Scene scene = new Scene("Level", null);
        Entity player = scene.createEntity("Player");
        SceneChanges sceneChanges = new SceneChanges(new Project(projectDirectory));
        sceneChanges.markSaved(scene);

        player.getComponent(TransformComponent.class).translate.x += 1.0f;

        assertTrue(sceneChanges.hasUnsavedChanges(scene));
    }

    @Test
    void addingAnEntityIsAnUnsavedChange() {
        Scene scene = new Scene("Level", null);
        SceneChanges sceneChanges = new SceneChanges(new Project(projectDirectory));
        sceneChanges.markSaved(scene);

        scene.createEntity("Coin");

        assertTrue(sceneChanges.hasUnsavedChanges(scene));
    }

    @Test
    void savingAgainClearsTheChanges() {
        Scene scene = new Scene("Level", null);
        SceneChanges sceneChanges = new SceneChanges(new Project(projectDirectory));
        sceneChanges.markSaved(scene);
        scene.createEntity("Coin");

        sceneChanges.markSaved(scene);

        assertFalse(sceneChanges.hasUnsavedChanges(scene));
    }

    @Test
    void noOpenSceneHasNoUnsavedChanges() {
        SceneChanges sceneChanges = new SceneChanges(new Project(projectDirectory));

        assertFalse(sceneChanges.hasUnsavedChanges(null));
    }
}
