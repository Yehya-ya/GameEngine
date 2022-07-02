package GameEngine.Engine.Listeners;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickButtons;

public class Joystick {
    private final int id;
    private final int numButtons, numAxes;
    private final boolean[] buttons;
    private final float[] axes;
    private String name;

    public Joystick(int id) {
        this.id = id;
        this.name = glfwGetJoystickName(id);
        this.numButtons = glfwGetJoystickButtons(id).capacity();
        this.numAxes = glfwGetJoystickAxes(id).capacity();
        this.buttons = new boolean[this.numButtons+1];
        this.axes = new float[this.numAxes+1];
    }

    public int getId() {
        return id;
    }

    public void update() {
        ByteBuffer byteBuffer = glfwGetJoystickButtons(this.id);

        if (byteBuffer != null){
            for (int i = 0; i <= this.numButtons; i++) {
                this.buttons[i] = byteBuffer.get(i) == GLFW_PRESS;
            }
        }

        FloatBuffer floatBuffer = glfwGetJoystickAxes(this.id);

        if (floatBuffer != null){
            for (int i = 0; i <= this.numAxes; i++) {
                this.axes[i] = floatBuffer.get(i);
            }
        }
    }

    public boolean isButtonPressed(int button) {
        return this.buttons[button];
    }

    public float getAxeValue(int axe) {
        return this.axes[axe];
    }
}
