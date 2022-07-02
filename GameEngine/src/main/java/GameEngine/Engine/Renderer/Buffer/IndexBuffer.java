package GameEngine.Engine.Renderer.Buffer;

import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Platforms.OpenGL.OpenGLIndexBuffer;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

public abstract class IndexBuffer {
    public static IndexBuffer create(int[] indices) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLIndexBuffer(indices);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public abstract void delete();

    public abstract void bind();

    public abstract void unbind();

    public abstract int getCount();
}
