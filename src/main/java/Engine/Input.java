package Engine;

import org.joml.Vector2d;

public abstract class Input {
    protected static Input instance;

    public static boolean isKeyPressed(int keyCode) {
        return instance.protectedIsKeyPressed(keyCode);
    }

    public static boolean isMouseButtonPressed(int mouseCode) {
        return instance.protectedIsMouseButtonPressed(mouseCode);
    }

    public static Vector2d getMousePosition() {
        return instance.protectedGetMousePosition();
    }

    public static double getMouseX() {
        return instance.protectedGetMousePosition().x;
    }

    public static double getMouseY() {
        return instance.protectedGetMousePosition().y;
    }

    protected abstract boolean protectedIsKeyPressed(int keyCode);

    protected abstract Vector2d protectedGetMousePosition();

    protected abstract boolean protectedIsMouseButtonPressed(int mouseCode);
}
