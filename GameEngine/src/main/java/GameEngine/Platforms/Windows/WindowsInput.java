package GameEngine.Platforms.Windows;

import static org.lwjgl.glfw.GLFW.*;

import GameEngine.Engine.Core.Input;
import java.nio.DoubleBuffer;
import org.joml.Vector2d;
import org.lwjgl.BufferUtils;

public class WindowsInput implements Input {
    private final long windowID;

    public WindowsInput(long windowID) {
        this.windowID = windowID;
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        int status = glfwGetKey(windowID, keyCode);
        return status == GLFW_PRESS || status == GLFW_REPEAT;
    }

    @Override
    public Vector2d getMousePosition() {
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(windowID, posX, posY);
        return new Vector2d(posX.get(0), posY.get(0));
    }

    @Override
    public boolean isMouseButtonPressed(int mouseCode) {
        int status = glfwGetMouseButton(windowID, mouseCode);
        return status == GLFW_PRESS;
    }
}
