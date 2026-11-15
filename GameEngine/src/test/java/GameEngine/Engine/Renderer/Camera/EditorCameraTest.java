package GameEngine.Engine.Renderer.Camera;

import static GameEngine.Engine.Utils.KeyCodes.YH_KEY_LEFT_ALT;
import static GameEngine.Engine.Utils.MouseCodes.GLFW_MOUSE_BUTTON_LEFT;
import static GameEngine.Engine.Utils.MouseCodes.GLFW_MOUSE_BUTTON_MIDDLE;
import static GameEngine.Engine.Utils.MouseCodes.GLFW_MOUSE_BUTTON_RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import GameEngine.Engine.Events.MouseEvents;
import GameEngine.Engine.Utils.TimeStep;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class EditorCameraTest {
    private static final float tolerance = 1e-5f;
    private static final TimeStep frame = new TimeStep(0.016f);
    private final FakeInput input = new FakeInput();
    private final EditorCamera camera = new EditorCamera(input);

    @Test
    void startsTenUnitsBehindItsFocalPoint() {
        assertEquals(new Vector3f(0.0f, 0.0f, 10.0f), camera.getPosition());
        assertEquals(10.0f, camera.getDistance());
    }

    @Test
    void draggingWithoutAltLeavesTheCameraAlone() {
        drag(GLFW_MOUSE_BUTTON_LEFT, 100, 50);
        drag(GLFW_MOUSE_BUTTON_MIDDLE, 200, 100);
        drag(GLFW_MOUSE_BUTTON_RIGHT, 300, 150);

        assertEquals(new Vector3f(0.0f, 0.0f, 10.0f), camera.getPosition());
        assertEquals(0.0f, camera.getYaw());
        assertEquals(0.0f, camera.getPitch());
        assertEquals(10.0f, camera.getDistance());
    }

    @Test
    void altAndLeftDragRotatesAroundTheFocalPoint() {
        input.holdKey(YH_KEY_LEFT_ALT);

        drag(GLFW_MOUSE_BUTTON_LEFT, 100, 50);

        assertEquals(100 * 0.003f * 0.8f, camera.getYaw(), tolerance);
        assertEquals(50 * 0.003f * 0.8f, camera.getPitch(), tolerance);
        assertEquals(10.0f, camera.getDistance());
        assertEquals(10.0f, camera.getPosition().length(), tolerance);
    }

    @Test
    void altAndMiddleDragPansAgainstTheMouse() {
        input.holdKey(YH_KEY_LEFT_ALT);

        drag(GLFW_MOUSE_BUTTON_MIDDLE, 100, 50);

        Vector3f position = camera.getPosition();
        assertTrue(position.x < 0.0f, "dragging right moves the camera left");
        assertTrue(position.y > 0.0f, "dragging down moves the camera up");
        assertEquals(10.0f, position.z, tolerance);
        assertEquals(0.0f, camera.getYaw());
        assertEquals(0.0f, camera.getPitch());
        assertEquals(10.0f, camera.getDistance());
    }

    @Test
    void altAndRightDragDownZoomsIn() {
        input.holdKey(YH_KEY_LEFT_ALT);

        drag(GLFW_MOUSE_BUTTON_RIGHT, 0, 100);

        float zoomSpeedAtDistanceTen = 4.0f;
        assertEquals(10.0f - 100 * 0.003f * zoomSpeedAtDistanceTen, camera.getDistance(), tolerance);
        assertEquals(camera.getDistance(), camera.getPosition().z, tolerance);
    }

    @Test
    void releasingAltStopsTheDrag() {
        input.holdKey(YH_KEY_LEFT_ALT);
        drag(GLFW_MOUSE_BUTTON_LEFT, 100, 50);
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();

        input.releaseKey(YH_KEY_LEFT_ALT);
        input.moveMouseTo(400, 300);
        camera.onUpdate(frame);

        assertEquals(yaw, camera.getYaw());
        assertEquals(pitch, camera.getPitch());
    }

    @Test
    void scrollingUpZoomsIn() {
        camera.onEvent(new MouseEvents.MouseScrolledEvent(0.0, 1.0));

        float zoomSpeedAtDistanceTen = 4.0f;
        assertEquals(10.0f - 0.1f * zoomSpeedAtDistanceTen, camera.getDistance(), tolerance);
        assertEquals(camera.getDistance(), camera.getPosition().z, tolerance);
    }

    @Test
    void zoomingPastTheFocalPointStopsOneUnitAwayAndMovesForward() {
        camera.onEvent(new MouseEvents.MouseScrolledEvent(0.0, 100.0));

        assertEquals(1.0f, camera.getDistance());
        assertEquals(new Vector3f(0.0f, 0.0f, 0.0f), camera.getPosition());
    }

    @Test
    void pressingAltAfterMovingTheMouseDoesNotJump() {
        input.moveMouseTo(400, 300);
        camera.onUpdate(frame);

        input.holdKey(YH_KEY_LEFT_ALT);
        input.holdMouseButton(GLFW_MOUSE_BUTTON_LEFT);
        camera.onUpdate(frame);

        assertEquals(0.0f, camera.getYaw());
        assertEquals(0.0f, camera.getPitch());
    }

    @Test
    void pressingAltAgainAfterMovingTheMouseDoesNotJump() {
        input.holdKey(YH_KEY_LEFT_ALT);
        drag(GLFW_MOUSE_BUTTON_LEFT, 100, 50);
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();
        input.releaseKey(YH_KEY_LEFT_ALT);
        input.moveMouseTo(600, 400);
        camera.onUpdate(frame);

        input.holdKey(YH_KEY_LEFT_ALT);
        camera.onUpdate(frame);

        assertEquals(yaw, camera.getYaw());
        assertEquals(pitch, camera.getPitch());
    }

    @Test
    void cameraMadeWhileTheMouseIsAwayFromTheCornerDoesNotJump() {
        input.moveMouseTo(400, 300);
        EditorCamera cameraMadeLater = new EditorCamera(input);

        input.holdKey(YH_KEY_LEFT_ALT);
        input.holdMouseButton(GLFW_MOUSE_BUTTON_LEFT);
        cameraMadeLater.onUpdate(frame);

        assertEquals(0.0f, cameraMadeLater.getYaw());
        assertEquals(0.0f, cameraMadeLater.getPitch());
    }

    private void drag(int mouseButton, double toX, double toY) {
        input.holdMouseButton(mouseButton);
        camera.onUpdate(frame);
        input.moveMouseTo(toX, toY);
        camera.onUpdate(frame);
    }
}
