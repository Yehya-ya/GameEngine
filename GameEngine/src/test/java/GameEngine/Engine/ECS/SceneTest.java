package GameEngine.Engine.ECS;

import static GameEngine.Engine.ECS.SceneAssertions.assertEntitiesMatch;
import static GameEngine.Engine.ECS.SceneAssertions.entityNamed;
import static GameEngine.Engine.ECS.SceneAssertions.uuids;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import GameEngine.Engine.ECS.Components.CameraComponent;
import GameEngine.Engine.ECS.Components.SpriteComponent;
import GameEngine.Engine.ECS.Components.TagComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import java.util.List;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

class SceneTest {

    @Test
    void copyKeepsEveryEntityWithItsIdAndValues() {
        Scene original = SampleScene.create();

        Scene copy = original.copy();

        assertEquals(uuids(original), uuids(copy));
        for (String name : List.of("Player", "Main Camera", "Eye")) {
            assertEntitiesMatch(entityNamed(original, name), entityNamed(copy, name));
        }
    }

    @Test
    void changingTheCopyLeavesTheOriginalAlone() {
        Scene original = SampleScene.create();
        Scene copy = original.copy();

        Entity copiedPlayer = entityNamed(copy, "Player");
        TransformComponent copiedTransform = copiedPlayer.getComponent(TransformComponent.class);
        copiedTransform.translate.set(9.0f);
        copiedTransform.size.set(9.0f);
        copiedTransform.rotation.set(9.0f);
        copiedPlayer.getComponent(SpriteComponent.class).color.set(0.0f);
        entityNamed(copy, "Main Camera")
                .getComponent(CameraComponent.class)
                .camera
                .getPosition()
                .set(9.0f);
        entityNamed(copy, "Eye")
                .getComponent(CameraComponent.class)
                .camera
                .getRotation()
                .set(9.0f);

        Entity player = entityNamed(original, "Player");
        TransformComponent transform = player.getComponent(TransformComponent.class);
        assertEquals(new Vector3f(1.5f, -2.25f, 0.1f), transform.translate);
        assertEquals(new Vector3f(2.0f, 3.0f, 1.0f), transform.size);
        assertEquals(new Vector3f(0.0f, 0.0f, 0.75f), transform.rotation);
        assertEquals(new Vector4f(0.2f, 0.4f, 0.6f, 1.0f), player.getComponent(SpriteComponent.class).color);
        assertEquals(
                new Vector3f(0.0f, 1.0f, 2.0f),
                entityNamed(original, "Main Camera")
                        .getComponent(CameraComponent.class)
                        .camera
                        .getPosition());
        assertEquals(
                new Vector3f(0.1f, 0.2f, 0.3f),
                entityNamed(original, "Eye")
                        .getComponent(CameraComponent.class)
                        .camera
                        .getRotation());
    }

    @Test
    void renamingInTheCopyLeavesTheOriginalName() {
        Scene original = SampleScene.create();
        Scene copy = original.copy();

        entityNamed(copy, "Player").getComponent(TagComponent.class).name = "Renamed";

        assertEquals("Player", entityNamed(original, "Player").getComponent(TagComponent.class).name);
    }

    @Test
    void copySharesTheScenesTextures() {
        Scene original = SampleScene.create();

        assertSame(original.getTextures(), original.copy().getTextures());
    }
}
