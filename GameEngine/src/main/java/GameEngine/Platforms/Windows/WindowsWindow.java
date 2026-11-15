package GameEngine.Platforms.Windows;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import GameEngine.Engine.Core.BuildType;
import GameEngine.Engine.Core.Input;
import GameEngine.Engine.Core.Window;
import GameEngine.Engine.Events.KeyEvent;
import GameEngine.Engine.Events.MouseEvents;
import GameEngine.Engine.Events.WindowEvents;
import GameEngine.Engine.Renderer.GraphicsContext;
import GameEngine.Platforms.OpenGL.OpenGLContext;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

public class WindowsWindow extends Window {
    private boolean GLFWInitialized;
    private long windowID;
    private long monitorID;
    private GraphicsContext graphicsContext;
    private final WindowsInput input;

    public WindowsWindow(Window.WindowProp prop) {
        super(prop);
        input = new WindowsInput(windowID);
        GLFWInitialized = false;
    }

    public long getWindowID() {
        return windowID;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    protected void init() {
        if (!GLFWInitialized) {
            boolean success = glfwInit();
            YH_ASSERT(success, "Could not initialize GLFW!");
            GLFWErrorCallback.createPrint(System.err);
            glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
            GLFWInitialized = true;
        }
        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, BuildType.current().isDebug() ? GLFW_TRUE : GLFW_FALSE);
        // glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        // glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

        // Create the window
        windowID = glfwCreateWindow(prop.width, prop.height, prop.title, NULL, NULL);
        YH_ASSERT(windowID != NULL, "Failed to create the GLFW window");

        monitorID = glfwGetPrimaryMonitor();

        // set input callback handlers
        glfwSetWindowSizeCallback(windowID, (window, width, height) -> {
            prop.width = width;
            prop.height = height;
            prop.eventCallBack.invoke(new WindowEvents.WindowResizeEvent(width, height));
        });
        glfwSetWindowCloseCallback(windowID, window -> prop.eventCallBack.invoke(new WindowEvents.WindowCloseEvent()));

        // key events
        glfwSetKeyCallback(windowID, (window, key, scancode, action, mods) -> {
            switch (action) {
                case GLFW_PRESS -> prop.eventCallBack.invoke(new KeyEvent.KeyPressedEvent(key, 0));
                case GLFW_RELEASE -> prop.eventCallBack.invoke(new KeyEvent.KeyReleasedEvent(key));
                case GLFW_REPEAT -> prop.eventCallBack.invoke(new KeyEvent.KeyPressedEvent(key, 1));
            }
        });
        glfwSetCharCallback(
                windowID, (window, codepoint) -> prop.eventCallBack.invoke(new KeyEvent.KeyTypedEvent(codepoint)));
        // mouse events
        glfwSetMouseButtonCallback(windowID, (window, button, action, mods) -> {
            switch (action) {
                case GLFW_PRESS -> prop.eventCallBack.invoke(new MouseEvents.MouseButtonPressedEvent(button));
                case GLFW_RELEASE -> prop.eventCallBack.invoke(new MouseEvents.MouseButtonReleasedEvent(button));
            }
        });
        glfwSetScrollCallback(
                windowID,
                (window, xoffset, yoffset) ->
                        prop.eventCallBack.invoke(new MouseEvents.MouseScrolledEvent(xoffset, yoffset)));
        glfwSetCursorPosCallback(
                windowID,
                (window, xpos, ypos) -> prop.eventCallBack.invoke(new MouseEvents.MouseMovedEvent(xpos, ypos)));

        graphicsContext = new OpenGLContext(windowID);
        graphicsContext.init();
        setVSync(false);
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
    public void setTitle(@NotNull String title) {
        super.setTitle(title);
        glfwSetWindowTitle(windowID, title);
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
