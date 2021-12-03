package Engine.Listeners;

import static org.lwjgl.glfw.GLFW.*;

public class JoystickListener {
    private static JoystickListener joystickListener = null;
    private boolean[] joysticks = new boolean[GLFW_JOYSTICK_LAST];


    private JoystickListener() {

    }

    public static JoystickListener get() {
        if (JoystickListener.joystickListener == null) {
            JoystickListener.joystickListener =  new JoystickListener();
        }

        return joystickListener;
    }

    public static void joystick_callback(int jid, int event)
    {
        if (event == GLFW_CONNECTED)
        {
            // The joystick was connected
            get().joysticks[jid] = true;
        }
        else if (event == GLFW_DISCONNECTED)
        {
            // The joystick was disconnected
            get().joysticks[jid] = false;
        }
    }
}
