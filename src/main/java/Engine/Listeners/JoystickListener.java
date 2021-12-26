package Engine.Listeners;

import static org.lwjgl.glfw.GLFW.*;

public class JoystickListener {
    private static JoystickListener joystickListener = null;
    private Joystick joystick;


    private JoystickListener() {
        this.joystick = null;
    }

    public static JoystickListener get() {
        if (JoystickListener.joystickListener == null) {
            JoystickListener.joystickListener = new JoystickListener();
        }

        return joystickListener;
    }

    public static void poll_controllers(){
        for (int i = 0; i < GLFW_JOYSTICK_LAST; i++) {
            int event = glfwJoystickPresent(i) ? GLFW_CONNECTED : GLFW_DISCONNECTED;
            joystick_callback(i ,event);
        }
    }

    public static void joystick_callback(int jid, int event) {
        if (event == GLFW_CONNECTED) {
            // The joystick was connected
            if (get().joystick == null) {
                get().joystick = new Joystick(jid);
            }
        } else if (event == GLFW_DISCONNECTED) {
            // The joystick was disconnected
            if (get().joystick != null && get().joystick.getId() == jid) {
                get().joystick = null;
                poll_controllers();
            }
        }
    }

    public static void update(){
        if (get().joystick != null) {
            get().joystick.update();
        }
    }

    public static boolean isButtonPressed(int button) {
        if (get().joystick != null) {
            return get().joystick.isButtonPressed(button);
        }
        return false;
    }
}
