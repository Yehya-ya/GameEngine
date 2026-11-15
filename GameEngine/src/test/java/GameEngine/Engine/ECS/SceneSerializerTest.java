package GameEngine.Engine.ECS;

import static GameEngine.Engine.ECS.SceneAssertions.assertEntitiesMatch;
import static GameEngine.Engine.ECS.SceneAssertions.entityNamed;
import static GameEngine.Engine.ECS.SceneAssertions.uuids;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import GameEngine.Engine.Core.Project;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SceneSerializerTest {
    @TempDir
    Path projectDirectory;

    @Test
    void savedSceneLoadsBackWithEveryEntityAndValue() {
        Scene original = SampleScene.create();

        Scene loaded = saveAndLoad(original);

        assertEquals(original.getTitle(), loaded.getTitle());
        assertEquals(uuids(original), uuids(loaded));
        for (String name : List.of("Player", "Main Camera", "Eye")) {
            assertEntitiesMatch(entityNamed(original, name), entityNamed(loaded, name));
        }
    }

    @Test
    void savingAndLoadingRememberWhereTheSceneIsSaved() {
        Scene scene = SampleScene.create();
        String path = projectDirectory.resolve("sample.yaml").toString();

        SceneSerializer.serialize(scene, path, project());
        Scene loaded = SceneSerializer.deserialize(path, project());

        assertEquals(path, scene.getUri());
        assertNotNull(loaded);
        assertEquals(path, loaded.getUri());
    }

    @Test
    void missingFileLoadsAsNoScene() {
        assertNull(SceneSerializer.deserialize(
                projectDirectory.resolve("missing.yaml").toString(), project()));
    }

    @Test
    void emptyFileLoadsAsNoScene() throws IOException {
        assertNull(load(""));
    }

    @Test
    void brokenYamlLoadsAsNoScene() throws IOException {
        assertNull(load("!Scene\nentities: [\n"));
    }

    @Test
    void unknownComponentLoadsAsNoScene() throws IOException {
        assertNull(load("""
                !Scene
                entities:
                - components:
                  - !UnknownComponent {name: Ghost}
                  entity: 0
                title: Unknown
                """));
    }

    @Test
    void sceneWithAMissingTextureLoadsAsNoScene() throws IOException {
        assertNull(load("""
                !Scene
                entities:
                - components:
                  - !SpriteComponent {color: null, texturePath: assets/textures/missing.png, tilingFactor: 1.0}
                  entity: 0
                title: Missing texture
                """));
    }

    private Scene saveAndLoad(Scene scene) {
        String path = projectDirectory.resolve("saved.yaml").toString();
        SceneSerializer.serialize(scene, path, project());
        Scene loaded = SceneSerializer.deserialize(path, project());
        assertNotNull(loaded);
        return loaded;
    }

    private Scene load(String fileContent) throws IOException {
        Path file = Files.writeString(projectDirectory.resolve("scene.yaml"), fileContent);
        return SceneSerializer.deserialize(file.toString(), project());
    }

    private Project project() {
        return new Project(projectDirectory);
    }
}
