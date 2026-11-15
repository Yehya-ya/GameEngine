package GameEngine.Engine.ECS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import GameEngine.Engine.ECS.Components.CameraComponent;
import GameEngine.Engine.ECS.Components.IdComponent;
import GameEngine.Engine.ECS.Components.SpriteComponent;
import GameEngine.Engine.ECS.Components.TagComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;
import GameEngine.Engine.Renderer.Camera.PerspectiveCamera;
import com.artemis.utils.IntBag;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

final class SceneAssertions {

    private SceneAssertions() {}

    static Entity entityNamed(Scene scene, String name) {
        IntBag ids = scene.getEntitiesIds(TagComponent.class);
        for (int i = 0; i < ids.size(); i++) {
            Entity entity = scene.getEntity(ids.get(i));
            if (entity.getComponent(TagComponent.class).name.equals(name)) {
                return entity;
            }
        }
        throw new AssertionError("The scene has no entity named " + name);
    }

    static Set<UUID> uuids(Scene scene) {
        Set<UUID> uuids = new HashSet<>();
        IntBag ids = scene.getEntitiesIds(IdComponent.class);
        for (int i = 0; i < ids.size(); i++) {
            uuids.add(scene.getEntity(ids.get(i)).getUuid());
        }
        return uuids;
    }

    static void assertEntitiesMatch(Entity expected, Entity actual) {
        assertEquals(expected.getUuid(), actual.getUuid());
        assertEquals(expected.getComponent(TagComponent.class).name, actual.getComponent(TagComponent.class).name);
        assertTransformsMatch(
                expected.getComponent(TransformComponent.class), actual.getComponent(TransformComponent.class));
        assertSpritesMatch(expected.getComponent(SpriteComponent.class), actual.getComponent(SpriteComponent.class));
        assertCamerasMatch(expected.getComponent(CameraComponent.class), actual.getComponent(CameraComponent.class));
    }

    private static void assertTransformsMatch(TransformComponent expected, TransformComponent actual) {
        assertEquals(expected.translate, actual.translate);
        assertEquals(expected.size, actual.size);
        assertEquals(expected.rotation, actual.rotation);
    }

    private static void assertSpritesMatch(SpriteComponent expected, SpriteComponent actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertEquals(expected.color, actual.color);
        assertEquals(expected.tilingFactor, actual.tilingFactor);
        assertEquals(expected.isTextured, actual.isTextured);
    }

    private static void assertCamerasMatch(CameraComponent expected, CameraComponent actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertEquals(expected.primary, actual.primary);
        assertEquals(expected.cameraType, actual.cameraType);
        assertCamerasMatch(expected.camera, actual.camera);
    }

    private static void assertCamerasMatch(Camera expected, Camera actual) {
        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected.getPosition(), actual.getPosition());
        assertEquals(expected.getRotation(), actual.getRotation());
        assertEquals(expected.getAspectRatio(), actual.getAspectRatio());
        assertEquals(expected.getNear(), actual.getNear());
        assertEquals(expected.getFar(), actual.getFar());
        if (expected instanceof OrthographicCamera orthographicCamera) {
            assertEquals(orthographicCamera.getZoomLevel(), ((OrthographicCamera) actual).getZoomLevel());
        }
        if (expected instanceof PerspectiveCamera perspectiveCamera) {
            assertEquals(perspectiveCamera.getFov(), ((PerspectiveCamera) actual).getFov());
        }
    }
}
