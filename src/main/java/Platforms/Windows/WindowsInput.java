package Platforms.Windows;

import Engine.Application;
import Engine.Input;
import org.joml.Vector2d;

import java.nio.DoubleBuffer;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static org.lwjgl.glfw.GLFW.*;

public class WindowsInput extends Input {
    private WindowsInput() {
    }

    public static void create() {
        YH_ASSERT(instance == null, "Input instance has already created.");
        instance = new WindowsInput();
    }

    @Override
    protected boolean protectedIsKeyPressed(int keyCode) {
        long windowID = Application.get().getWindow().getWindowID();
        int status = glfwGetKey(windowID, keyCode);
        return status == GLFW_PRESS || status == GLFW_REPEAT;
    }

    @Override
    protected Vector2d protectedGetMousePosition() {
        long windowID = Application.get().getWindow().getWindowID();
        DoubleBuffer posX = DoubleBuffer.allocate(1);
        DoubleBuffer posY = DoubleBuffer.allocate(1);
        glfwGetCursorPos(windowID, posX, posY);
        return new Vector2d(posX.get(0), posY.get(0));
    }

    @Override
    protected boolean protectedIsMouseButtonPressed(int mouseCode) {
        long windowID = Application.get().getWindow().getWindowID();
        int status = glfwGetMouseButton(windowID, mouseCode);
        return status == GLFW_PRESS;
    }
}
