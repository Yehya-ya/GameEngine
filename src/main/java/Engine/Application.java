package Engine;

import Engine.Events.*;
import Engine.Renderer.ShaderProgram;
import Engine.Utils.YH_Log;
import Platforms.OpenGL.OpenGLShaderProgram;
import Platforms.Windows.WindowsWindow;

import static Engine.Utils.KeyCodes.YH_KEY_F11;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.*;

public class Application {

    private static Application instance;

    private final WindowsWindow window;
    private final LayerStack layersStack;
    private final Layer imGuiLayer;
    private final int vertex_array;
    private final int vertex_buffer;
    private final ShaderProgram shaderProgram;
    private boolean isRunning;

    private Application() {
        YH_Log.YH_LOG_INFO("Creating the application.");
        window = new WindowsWindow(new Window.WindowProp());
        window.setEventCallback(this::onEvent);
        isRunning = true;
        imGuiLayer = new ImGuiLayer("ImGui layer");
        layersStack = new LayerStack();
        pushOverlay(imGuiLayer);

        shaderProgram = new OpenGLShaderProgram("vertexShader", "fragmentShader");

        float[] vertices = {-0.5f, -0.5f, 0.5f, -0.5f, 0.0f, 0.5f};

        vertex_array = glCreateVertexArrays();
        glBindVertexArray(vertex_array);

        vertex_buffer = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexArrayAttrib(vertex_array, 0);

    }

    public static Application get() {
        if (instance == null) {
            instance = new Application();
            return instance;
        }
        return instance;
    }

    public WindowsWindow getWindow() {
        return window;
    }

    public void run() {
        YH_Log.YH_LOG_INFO("The application starts running.");
        while (isRunning) {
            glClearColor(0.1f, 0.1f, 0.1f, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            shaderProgram.bind();
            glBindVertexArray(vertex_array);
            glDrawArrays(GL_TRIANGLES, 0, 6);

            for (Layer layer : layersStack) {
                layer.onUpdate();
            }

            window.onUpdate();
        }
        shaderProgram.destroy();
        window.destroy();
        YH_Log.YH_LOG_INFO("The application stops running.");
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
