package Engine;

import Engine.Events.*;
import Engine.ImGui.ImGuiLayer;
import Engine.Renderer.RendererCommandAPI;
import Engine.Utils.TimeStep;
import Platforms.Windows.WindowsWindow;
import org.joml.Vector4f;

import static Engine.Utils.KeyCodes.YH_KEY_F11;
import static Engine.Utils.YH_Log.YH_ASSERT;
import static Engine.Utils.YH_Log.YH_LOG_INFO;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Application {
    private static Application instance;

    private final WindowsWindow window;
    private final LayerStack layersStack;
    private final ImGuiLayer imGuiLayer;
    private boolean isRunning;
    private float lastFrameTime;

    protected Application() {
        YH_LOG_INFO("Creating the application.");
        YH_ASSERT(instance == null, "Application already exists!");
        instance = this;
        lastFrameTime = (float) glfwGetTime();
        window = new WindowsWindow(new Window.WindowProp());
        window.setEventCallback(this::onEvent);
        isRunning = true;

        RendererCommandAPI.init();
        RendererCommandAPI.SetClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RendererCommandAPI.clear();

        imGuiLayer = new ImGuiLayer();
        layersStack = new LayerStack();
        pushOverlay(imGuiLayer);
    }

    public static Application get() {
        return instance;
    }

    public WindowsWindow getWindow() {
        return window;
    }

    public void run() {
        YH_LOG_INFO("The application starts running.");
        while (isRunning) {
            float time = (float) glfwGetTime();
            TimeStep timeStep = new TimeStep(time - lastFrameTime);
            lastFrameTime = time;
            RendererCommandAPI.SetClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
            RendererCommandAPI.clear();

            for (Layer layer : layersStack) {
                layer.onUpdate(timeStep);
            }

            imGuiLayer.begin();
            for (Layer layer : layersStack) {
                layer.onImgRender();
            }
            imGuiLayer.end();

            window.onUpdate();
        }
        window.destroy();
        YH_LOG_INFO("The application stops running.");
    }

    public void onEvent(Event event) {
        new EventDispatcher(event).dispatch(EventType.WindowClose, event1 -> {
            WindowEvents.WindowCloseEvent e = (WindowEvents.WindowCloseEvent) event1;
            onWindowClose();
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

        for (int i = layersStack.size() - 1; i >= 0; i--) {
            if (event.handled) {
                break;
            }
            Layer layer = layersStack.get(i);
            layer.onEvent(event);
        }
    }

    public Layer getImGuiLayer() {
        return imGuiLayer;
    }

    public void pushLayer(Layer layer) {
        layersStack.pushLayer(layer);
    }

    public void pushOverlay(Layer layer) {
        layersStack.pushOverlay(layer);
    }

    private void onWindowClose() {
        isRunning = false;
    }
}
