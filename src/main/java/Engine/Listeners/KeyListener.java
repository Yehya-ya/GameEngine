package Engine.Listeners;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener keyListener = null;
    private boolean[] keyPressed = new boolean[GLFW_KEY_LAST];
    private boolean[] keyBeingPressed = new boolean[GLFW_KEY_LAST];

    private KeyListener() {

    }

    public static KeyListener get() {
        if (KeyListener.keyListener == null) {
            KeyListener.keyListener =  new KeyListener();
        }

        return keyListener;
    }

    public static void key_callback(long window, int key, int scancode, int action, int mods)
    {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
        } else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int key)
    {
        return get().keyPressed[key];
    }
}
