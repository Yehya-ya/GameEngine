package Engine.ImGui;

import Engine.Application;
import Engine.Layer;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import static Engine.Utils.YH_Log.YH_LOG_INFO;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImGuiLayer extends Layer {
    private final ImGuiImplGlfw imGuiImplGlfw;
    private final ImGuiImplGl3 imGuiImplGl3;

    public ImGuiLayer() {
        super("ImGuiLayer");
        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGl3 = new ImGuiImplGl3();
    }

    @Override
    public void onAttach() {
        YH_LOG_INFO("Attach ImGui Layer");
        YH_LOG_INFO("   ImGui Version: {}", ImGui.getVersion());
        ImGui.createContext();
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
            // style.Colors[ImGuiCol_WindowBg].w = 1.0f;
        }

        // Setup Platform/Renderer bindings
        imGuiImplGlfw.init(Application.get().getWindow().getWindowID(), true);
        imGuiImplGl3.init("#version 130");
    }

    @Override
    public void onDetach() {
        imGuiImplGl3.dispose();
        imGuiImplGlfw.dispose();
        ImGui.destroyContext();
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
}
