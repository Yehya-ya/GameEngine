package GameEngine.Engine.Renderer;

import GameEngine.Platforms.OpenGL.OpenGLUniformBuffer;
import org.jetbrains.annotations.NotNull;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

public abstract class UniformBuffer {
    public static @NotNull UniformBuffer create(int size, int binding) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLUniformBuffer(size, binding);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public abstract void delete();

    public void setData(float[] data) {
        setData(data, 0);
    }
    public abstract void setData(float[] data, long offset);
}
