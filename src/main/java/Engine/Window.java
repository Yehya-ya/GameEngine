package Engine;

import Engine.Listeners.JoystickListener;
import Engine.Listeners.KeyListener;
import Engine.Listeners.MouseListener;
import Engine.Scenes.Scene;
import Engine.Scenes.TestScene;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Window {
    private static Window window = null;
    private static Scene currentScene;
    private final String title;
    private int width, height;
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

    public static void close() {
        glfwSetWindowShouldClose(get().window_id, true);
    }

    public static boolean isFullScreen() {
        return get().isFullScreen;
    }

    public static void setFullScreen(boolean fullScreen) {
        if (fullScreen) {
            GLFWVidMode mode = glfwGetVideoMode(get().monitor_id);
            glfwSetWindowMonitor(get().window_id, get().monitor_id, 0, 0, mode.width(), mode.height(), mode.refreshRate());
        } else {
            glfwSetWindowMonitor(get().window_id, NULL, 0, 0, get().width, get().height, 60);
        }
        get().isFullScreen = fullScreen;
    }

    public static int getHeight() {
        return Window.get().height;
    }

    public static int getWidth() {
        return Window.get().width;
    }

    public static void changeScene(int scene) {
        switch (scene) {
            case TestScene.ID -> {
                Window.currentScene = new TestScene();
                Window.currentScene.init();
            }
            default -> {
            }
        }
    }

    private static void window_size_callback(long window, int width, int height) {
        Window.get().width = width;
        Window.get().height = height;
        glViewport(0, 0, width, height);
        currentScene.updateAspectRatio();
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

        glfwSetWindowSizeCallback(window_id, Window::window_size_callback);

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

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Window.changeScene(TestScene.ID);
    }

    private void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;
        // Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        glViewport(0, 0, Window.get().width, Window.get().height);
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window_id)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            JoystickListener.update();

            if (dt > 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(window_id); // swap the color buffers
            MouseListener.endFrame();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
