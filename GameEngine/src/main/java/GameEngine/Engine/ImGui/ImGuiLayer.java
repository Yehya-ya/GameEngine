package GameEngine.Engine.ImGui;

import GameEngine.Engine.Core.Application;
import GameEngine.Engine.Core.Layer;
import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Events.EventCategory;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImGuiLayer extends Layer {
    private final ImGuiImplGlfw imGuiImplGlfw;
    private final ImGuiImplGl3 imGuiImplGl3;
    private boolean isBlockingEvents;

    public ImGuiLayer() {
        super("ImGuiLayer");
        ImGui.createContext();
        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGl3 = new ImGuiImplGl3();
        isBlockingEvents = false;
    }

    @Override
    public void delete() {
        ImGui.destroyContext();
        super.delete();
    }

    @Override
    public void onAttach() {
        YH_LOG_TRACE("Attach ImGui Layer");
        YH_LOG_TRACE("   ImGui Version: {}", ImGui.getVersion());
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Enable Keyboard Controls
        // io.addConfigFlags(ImGuiConfigFlags.NavEnableGamepad); // Enable Gamepad Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable); // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable); // Enable Multi-Viewport / Platform Windows

        // Setup Dear ImGui style
        ImGui.styleColorsDark();

        // When viewports are enabled we tweak WindowRounding/WindowBg so platform windows can look identical to regular ones.
        ImGuiStyle style = ImGui.getStyle();
        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            style.setWindowRounding(0.0f);
        }
        setDarkThemeColors();

        // Setup Platform/Renderer bindings
        imGuiImplGlfw.init(Application.get().getWindow().getWindowID(), true);
        imGuiImplGl3.init("#version 130");
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if (isBlockingEvents) {
            ImGuiIO io = ImGui.getIO();
            event.handled |= io.getWantCaptureMouse() & event.isInCategory(EventCategory.Mouse);
            event.handled |= io.getWantCaptureKeyboard() & event.isInCategory(EventCategory.Keyboard);
        }
    }

    @Override
    public void onDetach() {
        imGuiImplGl3.dispose();
        imGuiImplGlfw.dispose();
    }

    public void begin() {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    public void end() {
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void setBlockingEvents(boolean blockingEvents) {
        isBlockingEvents = blockingEvents;
    }

    public void changeFont(String path, float size, boolean isDefault) {
        ImGuiIO io = ImGui.getIO();
        ImFont font = io.getFonts().addFontFromFileTTF(path, size);
        if (isDefault) {
            io.setFontDefault(font);
        }
    }

    public void setDarkThemeColors() {
        float[][] colors = ImGui.getStyle().getColors();
        colors[ImGuiCol.WindowBg] = new float[]{0.1f, 0.105f, 0.11f, 1.0f};

        // Headers
        colors[ImGuiCol.Header] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.HeaderHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.HeaderActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        // Buttons
        colors[ImGuiCol.Button] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.ButtonHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.ButtonActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        // Frame BG
        colors[ImGuiCol.FrameBg] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.FrameBgHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.FrameBgActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        // Tabs
        colors[ImGuiCol.Tab] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TabHovered] = new float[]{0.38f, 0.3805f, 0.381f, 1.0f};
        colors[ImGuiCol.TabActive] = new float[]{0.28f, 0.2805f, 0.281f, 1.0f};
        colors[ImGuiCol.TabUnfocused] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TabUnfocusedActive] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};

        // Title
        colors[ImGuiCol.TitleBg] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TitleBgActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TitleBgCollapsed] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        ImGui.getStyle().setColors(colors);
    }
}
