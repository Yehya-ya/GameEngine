package Engine;

import Engine.Listeners.JoystickListener;
import Engine.Listeners.KeyListener;
import Engine.Listeners.MouseListener;
import org.lwjgl.*;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;


public class Window {
    private static Window window = null;
    private final int width;
    private final int height;
    private final String title;
    private boolean isFullScreen;

    // The window handle
    private long window_id;
    private long monitor_id;

    private Window() {
        width = 500;
        height = 500;
        title = "Game Engine";
        isFullScreen = false;
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    private static void close() {
        glfwSetWindowShouldClose(get().window_id, true);
    }

    static public boolean isFullScreen() {
        return get().isFullScreen;
    }

    static public void setFullScreen(boolean fullScreen) {
        if (fullScreen) {
            GLFWVidMode mode = glfwGetVideoMode(get().monitor_id);
            glfwSetWindowMonitor(get().window_id, get().monitor_id, 0, 0, mode.width(), mode.height(), mode.refreshRate());
        } else {
            glfwSetWindowMonitor(get().window_id, NULL, 0, 0, get().width, get().height, 60);
        }
        get().isFullScreen = fullScreen;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window_id);
        glfwDestroyWindow(window_id);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        monitor_id = glfwGetPrimaryMonitor();

        // Create the window
        window_id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window_id == NULL) throw new RuntimeException("Failed to create the GLFW window");

        // set input callback handlers
        glfwSetCursorPosCallback(window_id, MouseListener::cursor_position_callback);
        glfwSetMouseButtonCallback(window_id, MouseListener::mouse_button_callback);
        glfwSetScrollCallback(window_id, MouseListener::scroll_callback);
        glfwSetKeyCallback(window_id, KeyListener::key_callback);
        glfwSetJoystickCallback(JoystickListener::joystick_callback);
        JoystickListener.poll_controllers();

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window_id, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(window_id, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window_id);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window_id);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window_id)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glfwSwapBuffers(window_id); // swap the color buffers


            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            JoystickListener.update();

            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                System.out.println("ddfdf");
            }

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

            MouseListener.endFrame();
        }
    }
}
