package Engine;

import Engine.Events.Event;
import Engine.Events.EventDispatcher;
import Engine.Events.EventType;
import Engine.Events.WindowEvents;
import Platforms.Windows.WindowsWindow;

public class Application {

    private static Application instance;

    private final WindowsWindow window;
    private final LayerStack layersStack;
    private final Layer imGuiLayer;
    private boolean isRunning;

    public static Application get() {
        if (instance != null) {
            return instance;
        }
        return new Application();
    }

    private Application() {
        window = new WindowsWindow(new Window.WindowProp());
        window.setEventCallback(this::onEvent);
        isRunning = true;
        imGuiLayer = new ImGuiLayer("ImGui layer");
        layersStack = new LayerStack();
        pushOverlay(imGuiLayer);
        YH_Log.info("Application Created.");
    }

    public WindowsWindow getWindow() {
        return window;
    }

    public void run() {
        YH_Log.info("Application starts running.");
        while (isRunning) {
            for (Layer layer : layersStack) {
                layer.onUpdate();
            }

            window.onUpdate();
        }
        YH_Log.info("Application stops running.");
    }

    public void onEvent(Event event) {
        YH_Log.info(event.toString());
        new EventDispatcher(event).dispatch(EventType.WindowClose, event1 -> {
            WindowEvents.WindowCloseEvent e = (WindowEvents.WindowCloseEvent) event1;
            onWindowClose();
            e.handled = true;
            return true;
        });

        for (int i = layersStack.size()-1; i>=0; i--) {
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
