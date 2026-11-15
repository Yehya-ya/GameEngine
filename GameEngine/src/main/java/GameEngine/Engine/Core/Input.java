package GameEngine.Engine.Core;

import org.joml.Vector2d;

public interface Input {
    boolean isKeyPressed(int keyCode);

    boolean isMouseButtonPressed(int mouseCode);

    Vector2d getMousePosition();
}
