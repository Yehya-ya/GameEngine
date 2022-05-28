package Platforms.Windows;

import Engine.Events.KeyEvent;
import Engine.Events.MouseEvents;
import Engine.Events.WindowEvents;
import Engine.Renderer.GraphicsContext;
import Engine.Window;
import Engine.YH_Log;
import Platforms.OpenGL.OpenGLContext;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowsWindow extends Window {
    private boolean GLFWInitialized;
    private long window_id;
    private long monitor_id;
    private GraphicsContext graphicsContext;

    public WindowsWindow(Window.WindowProp prop) {
        super(prop);
        GLFWInitialized = false;
    }

    @Override
    public void init() {
        if (!GLFWInitialized)
        {
            // TODO: glfwTerminate on system shutdown
            boolean success = glfwInit();
            YH_Log._assert(success, "Could not initialize GLFW!");
            glfwSetErrorCallback((error, description) -> YH_Log.error("GLFW Error ({0}): {1}", error, description));
            GLFWInitialized = true;
        }
        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window_id = glfwCreateWindow(prop.width, prop.height, prop.title, NULL, NULL);
        YH_Log._assert(window_id != NULL, "Failed to create the GLFW window");

        // set input callback handlers
        glfwSetWindowSizeCallback(window_id, (window, width, height) -> prop.eventCallBack.invoke(new WindowEvents.WindowResizeEvent(width, height)));
        glfwSetWindowCloseCallback(window_id, window -> prop.eventCallBack.invoke(new WindowEvents.WindowCloseEvent()));

        // key events
        glfwSetKeyCallback(window_id, (window, key, scancode, action, mods) -> {
            switch (action) {
                case GLFW_PRESS -> prop.eventCallBack.invoke(new KeyEvent.KeyPressedEvent(key,0));
                case GLFW_RELEASE -> prop.eventCallBack.invoke(new KeyEvent.KeyReleasedEvent(key));
                case GLFW_REPEAT -> prop.eventCallBack.invoke(new KeyEvent.KeyPressedEvent(key, 1));
            }
        });
        glfwSetCharCallback(window_id, (window, codepoint) -> prop.eventCallBack.invoke(new KeyEvent.KeyTypedEvent(codepoint)));
        // mouse events
        glfwSetMouseButtonCallback(window_id, (window, button, action, mods) -> {
            switch (action) {
                case GLFW_PRESS -> prop.eventCallBack.invoke(new MouseEvents.MouseButtonPressedEvent(button));
                case GLFW_RELEASE -> prop.eventCallBack.invoke(new MouseEvents.MouseButtonReleasedEvent(button));
            }
        });
        glfwSetScrollCallback(window_id, (window, xoffset, yoffset) -> prop.eventCallBack.invoke(new MouseEvents.MouseScrolledEvent(xoffset, yoffset)));
        glfwSetCursorPosCallback(window_id, (window, xpos, ypos) -> prop.eventCallBack.invoke(new MouseEvents.MouseMovedEvent(xpos, ypos)));

        //JoystickListener.poll_controllers();

        graphicsContext = new OpenGLContext(window_id);
        graphicsContext.init();
        setVSync(prop.vSync);
        glfwShowWindow(window_id);
    }

    @Override
    public void onUpdate() {
        glfwPollEvents();
        graphicsContext.swapBuffers();
    }

    @Override
    public void shutdown() {
        glfwFreeCallbacks(window_id);
        glfwDestroyWindow(window_id);
    }

    @Override
    public void toggleFullScreen() {
        super.toggleFullScreen();
        prop.isFullScreen = !prop.isFullScreen;
        if (prop.isFullScreen) {
            GLFWVidMode mode = glfwGetVideoMode(monitor_id);
            if (mode != null) {
                prop.width = mode.width();
                prop.height = mode.height();
                glfwSetWindowMonitor(window_id, monitor_id, 0, 0, prop.width, prop.height, mode.refreshRate());
                return;
            }
        }

        glfwSetWindowMonitor(window_id, NULL, 0, 0, prop.width, prop.height, 60);
    }

    @Override
    public void setVSync(boolean vSync) {
        super.setVSync(vSync);
        if (prop.vSync){
            glfwSwapInterval(1);
        }
        else{
            glfwSwapInterval(0);
        }
    }
}
