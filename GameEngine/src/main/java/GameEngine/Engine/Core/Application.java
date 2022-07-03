package GameEngine.Engine.Core;

import GameEngine.Engine.Events.*;
import GameEngine.Engine.ImGui.ImGuiLayer;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Utils.TimeStep;
import GameEngine.Platforms.Windows.WindowsWindow;

import static GameEngine.Engine.Utils.KeyCodes.YH_KEY_F11;
import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Application {
    private static Application instance;

    private final WindowsWindow window;
    private final LayerStack layersStack;
    private final ImGuiLayer imGuiLayer;
    private boolean isRunning;
    private boolean isMinimized;
    private float lastFrameTime;

    protected Application() {
        YH_LOG_TRACE("Creating the application.");
        YH_ASSERT(instance == null, "Application already exists!");
        instance = this;
        lastFrameTime = (float) glfwGetTime();
        window = new WindowsWindow(new Window.WindowProp());
        window.setEventCallback(this::onEvent);
        isRunning = true;
        isMinimized = false;

        RendererCommandAPI.init();

        layersStack = new LayerStack();
        imGuiLayer = new ImGuiLayer();
        pushOverlay(imGuiLayer);
    }

    public static Application get() {
        return instance;
    }

    public WindowsWindow getWindow() {
        return window;
    }

    public void shutdown() {
    }

    public void run() {
        YH_LOG_TRACE("The application starts running.");
        while (isRunning) {
            float time = (float) glfwGetTime();
            TimeStep timeStep = new TimeStep(time - lastFrameTime);
            lastFrameTime = time;

            if (!isMinimized) {
                for (Layer layer : layersStack) {
                    layer.onUpdate(timeStep);
                }
            }

            imGuiLayer.begin();
            for (Layer layer : layersStack) {
                layer.onImgRender();
            }
            imGuiLayer.end();

            window.onUpdate();
        }
        YH_LOG_TRACE("The application stops running.");
        this.delete();
    }

    public void onEvent(Event event) {
        new EventDispatcher(event).dispatch(EventType.WindowClose, event1 -> {
            WindowEvents.WindowCloseEvent e = (WindowEvents.WindowCloseEvent) event1;
            close();
            e.handled = true;
            return true;
        });

        new EventDispatcher(event).dispatch(EventType.KeyPressed, event1 -> {
            KeyEvent.KeyPressedEvent e = (KeyEvent.KeyPressedEvent) event1;
            switch (e.getKeyCode()) {
                case YH_KEY_F11 -> window.toggleFullScreen();
            }
            return false;
        });

        new EventDispatcher(event).dispatch(EventType.WindowResize, event1 -> {
            WindowEvents.WindowResizeEvent e = (WindowEvents.WindowResizeEvent) event1;
            if (e.getHeight() == 0 || e.getWidth() == 0) {
                isMinimized = true;
                return false;
            }

            RendererCommandAPI.setViewport(e.getWidth(), e.getHeight());
            isMinimized = false;
            return false;
        });

        for (int i = layersStack.size() - 1; i >= 0; i--) {
            if (event.handled) {
                break;
            }
            Layer layer = layersStack.get(i);
            layer.onEvent(event);
        }
    }

    public ImGuiLayer getImGuiLayer() {
        return imGuiLayer;
    }

    public void pushLayer(Layer layer) {
        layersStack.pushLayer(layer);
    }

    public void pushOverlay(Layer layer) {
        layersStack.pushOverlay(layer);
    }

    public void popLayer(Layer layer) {
        layersStack.popLayer(layer);
    }

    public void popOverlay(Layer layer) {
        layersStack.popOverlay(layer);
    }

    public void close() {
        isRunning = false;
    }

    private void delete() {
        layersStack.delete();
        imGuiLayer.delete();
        this.shutdown();
        window.delete();
        instance = null;
        YH_LOG_TRACE("Deleting the application.");
    }
}
