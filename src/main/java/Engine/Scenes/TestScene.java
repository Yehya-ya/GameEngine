package Engine.Scenes;

import Engine.Listeners.JoystickListener;
import Engine.Listeners.KeyListener;
import Engine.Renderer.Renderer;
import Engine.Window;

import static org.lwjgl.glfw.GLFW.*;

public class TestScene extends Scene {
    public static final int ID = 101;

    private final Renderer renderer;

    public TestScene() {
        super();
        this.renderer = new Renderer();
    }

    @Override
    public void init() {
        this.renderer.init();
    }

    @Override
    public void update(float dt) {
        this.renderer.render();

        if (KeyListener.isKeyPressed(GLFW_KEY_F1)) {
            boolean isFullScreen = Window.isFullScreen();
            Window.setFullScreen(!isFullScreen);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.close();
        }

        if (JoystickListener.isButtonPressed(GLFW_GAMEPAD_BUTTON_A)) {
            System.out.println("A");
        }

        if (JoystickListener.isButtonPressed(GLFW_GAMEPAD_BUTTON_B)) {
            System.out.println("B");
        }

        if (JoystickListener.isButtonPressed(GLFW_GAMEPAD_BUTTON_X)) {
            System.out.println("X");
        }

        if (JoystickListener.isButtonPressed(GLFW_GAMEPAD_BUTTON_Y)) {
            System.out.println("Y");
        }

        if (JoystickListener.isButtonPressed(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)) {
            System.out.println("LEFT_BUMPER");
        }

        if (JoystickListener.isButtonPressed(GLFW_GAMEPAD_BUTTON_LEFT_THUMB)) {
            System.out.println("LEFT_THUMB");
        }

        if (JoystickListener.isButtonPressed(GLFW_GAMEPAD_BUTTON_DPAD_LEFT)) {
            System.out.println("DPAD_LEFT");
        }
    }
}
