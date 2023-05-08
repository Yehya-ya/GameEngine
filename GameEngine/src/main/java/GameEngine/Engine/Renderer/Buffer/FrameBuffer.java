package GameEngine.Engine.Renderer.Buffer;

import GameEngine.Engine.Renderer.RendererCommand;
import GameEngine.Engine.Renderer.RendererCommandAPI;
import GameEngine.Platforms.OpenGL.OpenGLFrameBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

public abstract class FrameBuffer {
    public static @NotNull FrameBuffer create(FrameBufferSpecification specification) {
        if (Objects.requireNonNull(RendererCommandAPI.getApi()) == RendererCommand.API.OpenGL) {
            return new OpenGLFrameBuffer(specification);
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public int getColorAttachmentRendererId() {
        return getColorAttachmentRendererId(0);
    }

    public abstract FrameBufferSpecification getSpecification();

    public abstract int getColorAttachmentRendererId(int index);

    public abstract void delete();

    public abstract void bind();

    public abstract void unbind();

    public abstract void resize(int width, int height);

    public abstract int ReadPixel(int attachmentIndex, int x, int y);

    public abstract void clearAttachment(int attachmentIndex, int value);

    public enum FramebufferTextureFormat {
        None(0), RGBA8(1), RED_INTEGER(2), DEPTH24STENCIL8(3), Depth(DEPTH24STENCIL8.value);

        public final int value;

        FramebufferTextureFormat(int value) {
            this.value = value;
        }
    }

    public static class FrameBufferSpecification {
        public int width, height;
        public int samples = 1;
        public FramebufferAttachmentSpecification attachments;
        public boolean swapChainTarget = false;
    }

    public static class FramebufferAttachmentSpecification {
        public List<FramebufferTextureSpecification> attachments;

        public FramebufferAttachmentSpecification() {
            attachments = new ArrayList<>();
        }

        public void addAttachments(FramebufferTextureSpecification textureSpecification) {
            attachments.add(textureSpecification);
        }
    }

    public static class FramebufferTextureSpecification {
        public FramebufferTextureFormat textureFormat;

        public FramebufferTextureSpecification() {
            this(FramebufferTextureFormat.None);
        }

        public FramebufferTextureSpecification(FramebufferTextureFormat format) {
            this.textureFormat = format;
        }
    }
}
