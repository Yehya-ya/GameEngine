package GameEngine.Engine.Renderer.Camera;

import GameEngine.Engine.Core.Input;
import java.util.HashSet;
import java.util.Set;
import org.joml.Vector2d;

final class FakeInput implements Input {
    private final Set<Integer> heldKeys = new HashSet<>();
    private final Set<Integer> heldMouseButtons = new HashSet<>();
    private final Vector2d mousePosition = new Vector2d();

    void holdKey(int keyCode) {
        heldKeys.add(keyCode);
    }

    void releaseKey(int keyCode) {
        heldKeys.remove(keyCode);
    }

    void holdMouseButton(int mouseCode) {
        heldMouseButtons.add(mouseCode);
    }

    void moveMouseTo(double x, double y) {
        mousePosition.set(x, y);
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        return heldKeys.contains(keyCode);
    }

    @Override
    public boolean isMouseButtonPressed(int mouseCode) {
        return heldMouseButtons.contains(mouseCode);
    }

    @Override
    public Vector2d getMousePosition() {
        return new Vector2d(mousePosition);
    }
}
