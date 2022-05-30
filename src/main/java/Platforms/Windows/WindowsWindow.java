package Platforms.Windows;

import Engine.Events.KeyEvent;
import Engine.Events.MouseEvents;
import Engine.Events.WindowEvents;
import Engine.Renderer.GraphicsContext;
import Engine.Window;
import Platforms.OpenGL.OpenGLContext;
import org.lwjgl.glfw.GLFWVidMode;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static Engine.Utils.YH_Log.YH_LOG_ERROR;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowsWindow extends Window {
    private boolean GLFWInitialized;
    private long windowID;
    private long monitorID;
    private GraphicsContext graphicsContext;

    public WindowsWindow(Window.WindowProp prop) {
        super(prop);
        WindowsInput.create();
        GLFWInitialized = false;
    }

    public long getWindowID() {
        return windowID;
    }

    @Override
    protected void init() {
        if (!GLFWInitialized) {
            // TODO: glfwTerminate on system shutdown
            boolean success = glfwInit();
            YH_ASSERT(success, "Could not initialize GLFW!");
            glfwSetErrorCallback((error, description) -> YH_LOG_ERROR("GLFW Error ({0}): {1}", error, description));
            GLFWInitialized = true;
        }
        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        windowID = glfwCreateWindow(prop.width, prop.height, prop.title, NULL, NULL);
        YH_ASSERT(windowID != NULL, "Failed to create the GLFW window");

        monitorID = glfwGetPrimaryMonitor();

        // set input callback handlers
        glfwSetWindowSizeCallback(windowID, (window, width, height) -> prop.eventCallBack.invoke(new WindowEvents.WindowResizeEvent(width, height)));
        glfwSetWindowCloseCallback(windowID, window -> prop.eventCallBack.invoke(new WindowEvents.WindowCloseEvent()));

        // key events
        glfwSetKeyCallback(windowID, (window, key, scancode, action, mods) -> {
            switch (action) {
                case GLFW_PRESS -> prop.eventCallBack.invoke(new KeyEvent.KeyPressedEvent(key, 0));
                case GLFW_RELEASE -> prop.eventCallBack.invoke(new KeyEvent.KeyReleasedEvent(key));
                case GLFW_REPEAT -> prop.eventCallBack.invoke(new KeyEvent.KeyPressedEvent(key, 1));
            }
        });
        glfwSetCharCallback(windowID, (window, codepoint) -> prop.eventCallBack.invoke(new KeyEvent.KeyTypedEvent(codepoint)));
        // mouse events
        glfwSetMouseButtonCallback(windowID, (window, button, action, mods) -> {
            switch (action) {
                case GLFW_PRESS -> prop.eventCallBack.invoke(new MouseEvents.MouseButtonPressedEvent(button));
                case GLFW_RELEASE -> prop.eventCallBack.invoke(new MouseEvents.MouseButtonReleasedEvent(button));
            }
        });
        glfwSetScrollCallback(windowID, (window, xoffset, yoffset) -> prop.eventCallBack.invoke(new MouseEvents.MouseScrolledEvent(xoffset, yoffset)));
        glfwSetCursorPosCallback(windowID, (window, xpos, ypos) -> prop.eventCallBack.invoke(new MouseEvents.MouseMovedEvent(xpos, ypos)));

        //JoystickListener.poll_controllers();

        graphicsContext = new OpenGLContext(windowID);
        graphicsContext.init();
        setVSync(prop.vSync);
        glfwShowWindow(windowID);
    }

    @Override
    public void onUpdate() {
        graphicsContext.swapBuffers();
        glfwPollEvents();
    }

    @Override
    protected void shutdown() {
        glfwFreeCallbacks(windowID);
        glfwDestroyWindow(windowID);
        glfwTerminate();
    }

    @Override
    public void toggleFullScreen() {
        super.toggleFullScreen();
        if (prop.isFullScreen) {
            GLFWVidMode mode = glfwGetVideoMode(monitorID);
            if (mode != null) {
                prop.width = mode.width();
                prop.height = mode.height();
                glfwSetWindowMonitor(windowID, monitorID, 0, 0, prop.width, prop.height, mode.refreshRate());
                return;
            }
        }
        prop.width = 1280;
        prop.height = 720;

        glfwSetWindowMonitor(windowID, NULL, 100, 100, prop.width, prop.height, 60);
    }

    @Override
    public void setVSync(boolean vSync) {
        super.setVSync(vSync);
        if (prop.vSync) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
    }
}
