package Platforms.OpenGL;

import Engine.Renderer.GraphicsContext;
import Engine.Utils.YH_Log;
import org.lwjgl.opengl.GL;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLContext extends GraphicsContext {
    private final long windowID;

    public OpenGLContext(long windowID) {
        this.windowID = windowID;

        YH_ASSERT(windowID != NULL, "Window id is null!");
    }

    @Override
    public void init() {
        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();

        YH_Log.YH_LOG_INFO("OpenGL Info:");
        YH_Log.YH_LOG_INFO("   Vendor: {}", glGetString(GL_VENDOR));
        YH_Log.YH_LOG_INFO("   Renderer: {}", glGetString(GL_RENDERER));
        YH_Log.YH_LOG_INFO("   Version: {}", glGetString(GL_VERSION));
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(this.windowID);
    }
}
