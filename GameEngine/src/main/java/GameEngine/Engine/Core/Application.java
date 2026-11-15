package GameEngine.Engine.Core;

import static GameEngine.Engine.Utils.KeyCodes.YH_KEY_F11;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

import GameEngine.Engine.Events.*;
import GameEngine.Engine.Renderer.BatchRenderer2D;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Engine.Utils.TimeStep;
import GameEngine.Platforms.Windows.WindowsWindow;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Application {
    private final WindowsWindow window;
    private final LayerStack layersStack;
    private final BatchRenderer2D renderer2D;
    private final List<Runnable> actionsBeforeNextFrame = new ArrayList<>();
    private boolean isRunning;
    private boolean isMinimized;
    private double lastFrameTime;

    protected Application(String name) {
        YH_LOG_TRACE("Creating the application.");
        lastFrameTime = glfwGetTime();
        window = new WindowsWindow(new Window.WindowProp(name));
        window.setEventCallback(this::onEvent);
        isRunning = true;
        isMinimized = false;

        RendererCommandAPI.init();
        renderer2D = new BatchRenderer2D();

        layersStack = new LayerStack();
    }

    public WindowsWindow getWindow() {
        return window;
    }

    public BatchRenderer2D getRenderer2D() {
        return renderer2D;
    }

    public void shutdown() {}

    public void runBeforeNextFrame(@NotNull Runnable action) {
        actionsBeforeNextFrame.add(action);
    }

    public void run() {
        YH_LOG_TRACE("The application starts running.");
        while (isRunning) {
            runActionsBeforeThisFrame();
            double time = glfwGetTime();
            TimeStep timeStep = new TimeStep((float) (time - lastFrameTime));
            lastFrameTime = time;

            if (!isMinimized) {
                for (Layer layer : layersStack) {
                    layer.onUpdate(timeStep);
                }
            }

            beginGui();
            for (Layer layer : layersStack) {
                layer.onGuiRender();
            }
            endGui();

            window.onUpdate();
        }
        YH_LOG_TRACE("The application stops running.");
        this.delete();
    }

    protected void beginGui() {}

    protected void endGui() {}

    private void runActionsBeforeThisFrame() {
        List<Runnable> actions = List.copyOf(actionsBeforeNextFrame);
        actionsBeforeNextFrame.clear();
        actions.forEach(Runnable::run);
    }

    public void onEvent(Event event) {
        new EventDispatcher(event).dispatch(EventType.WindowClose, event1 -> {
            WindowEvents.WindowCloseEvent e = (WindowEvents.WindowCloseEvent) event1;
            runBeforeNextFrame(this::requestClose);
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

    public void requestClose() {
        for (Layer layer : layersStack) {
            if (!layer.confirmClose()) {
                return;
            }
        }
        close();
    }

    public void close() {
        isRunning = false;
    }

    private void delete() {
        layersStack.delete();
        this.shutdown();
        renderer2D.delete();
        window.delete();
        YH_LOG_TRACE("Deleting the application.");
    }
}
