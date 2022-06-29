package Platforms.OpenGL;

import Engine.Renderer.GraphicsContext;
import org.lwjgl.opengl.GL;

import static Engine.Utils.YH_Log.*;
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

        YH_LOG_TRACE("OpenGL Info:");
        YH_LOG_TRACE("   Vendor: {}", glGetString(GL_VENDOR));
        YH_LOG_TRACE("   Renderer: {}", glGetString(GL_RENDERER));
        YH_LOG_TRACE("   Version: {}", glGetString(GL_VERSION));
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(this.windowID);
    }
}
