package Platforms.OpenGL;

import Engine.Renderer.GraphicsContext;
import Engine.YH_Log;
import org.lwjgl.opengl.GL;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;

public class OpenGLContext extends GraphicsContext {
    private final long windowID;

    public OpenGLContext(long windowID) {
        this.windowID = windowID;

        YH_Log._assert(windowID != NULL, "Window id is null!");
    }

    @Override
    public void init() {
        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();

        YH_Log.info("OpenGL Info:");
        YH_Log.info("   Vendor: {}", glGetString(GL_VENDOR));
        YH_Log.info("   Renderer: {}", glGetString(GL_RENDERER));
        YH_Log.info("   Version: {}", glGetString(GL_VERSION));
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(this.windowID);
    }
}
