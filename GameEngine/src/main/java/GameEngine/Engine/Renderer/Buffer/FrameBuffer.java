package GameEngine.Engine.Renderer.Buffer;

import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Platforms.OpenGL.OpenGLFrameBuffer;
import org.jetbrains.annotations.NotNull;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

public abstract class FrameBuffer {
    protected FrameBufferSpecification specification;
    protected int rendererId, colorAttachmentRendererId, depthAttachmentRendererId;

    public FrameBuffer(FrameBufferSpecification specification) {
        this.specification = specification;
        rendererId = colorAttachmentRendererId = depthAttachmentRendererId = 0;
    }

    public static @NotNull FrameBuffer create(FrameBufferSpecification specification) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLFrameBuffer(specification);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public FrameBufferSpecification getSpecification() {
        return specification;
    }

    public int getColorAttachmentRendererId() {
        return colorAttachmentRendererId;
    }

    public abstract void delete();

    public abstract void bind();

    public abstract void unbind();

    public abstract void resize(int width, int height);

    public static class FrameBufferSpecification {
        public int width, height;
        public int sample = 1;
        public boolean swapChainTarget = false;
    }
}
