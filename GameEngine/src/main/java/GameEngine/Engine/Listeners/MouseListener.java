package GameEngine.Engine.Listeners;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener mouseListener = null;
    private double scrollX, scrollY;
    private double posX, posY, lastX, lastY;
    private final boolean[] mousePressedButtons = new boolean[3];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.posX = 0.0;
        this.posY = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
        this.isDragging = false;
    }

    public static MouseListener get() {
        if (MouseListener.mouseListener == null) {
            MouseListener.mouseListener = new MouseListener();
        }

        return mouseListener;
    }

    public static void cursor_position_callback(long window, double pos_x, double pos_y) {
        MouseListener.get().lastX = MouseListener.get().posX;
        MouseListener.get().lastY = MouseListener.get().posY;
        MouseListener.get().posX = pos_x;
        MouseListener.get().posY = pos_y;

        for (boolean mouseButton : MouseListener.get().mousePressedButtons) {
            if (mouseButton) {
                MouseListener.get().isDragging = true;
                break;
            }
        }
    }

    public static void mouse_button_callback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < MouseListener.get().mousePressedButtons.length) {
                MouseListener.get().mousePressedButtons[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if (button < MouseListener.get().mousePressedButtons.length) {
                MouseListener.get().mousePressedButtons[button] = false;
                MouseListener.get().isDragging = false;
            }
        }
    }

    public static void scroll_callback(long window, double offset_x, double offset_y) {
        MouseListener.get().scrollX = offset_x;
        MouseListener.get().scrollY = offset_y;
    }

    public static void endFrame() {
        MouseListener.get().scrollX = 0;
        MouseListener.get().scrollY = 0;
        MouseListener.get().lastX = MouseListener.get().posX;
        MouseListener.get().lastY = MouseListener.get().posY;
    }

    static public double getDX() {
        return MouseListener.get().lastX - MouseListener.get().posX;
    }

    static public double getDY() {
        return MouseListener.get().lastY - MouseListener.get().posY;
    }

    static public double getPosX() {
        return MouseListener.get().posX;
    }

    static public double getPosY() {
        return MouseListener.get().posY;
    }

    static public double getScrollX() {
        return MouseListener.get().scrollX;
    }

    static public double getScrollY() {
        return MouseListener.get().scrollY;
    }
}
