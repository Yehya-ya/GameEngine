package GameEngine.Platforms.OpenGL;

import GameEngine.Engine.Renderer.Buffer.FrameBuffer;
import GameEngine.Engine.Renderer.RendererCommandAPI;

import java.nio.ByteBuffer;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL45.*;

public class OpenGLFrameBuffer extends FrameBuffer {
    public OpenGLFrameBuffer(FrameBufferSpecification specification) {
        super(specification);
        invalidate();
    }

    @Override
    public void delete() {
        glDeleteFramebuffers(rendererId);
        glDeleteTextures(colorAttachmentRendererId);
        glDeleteTextures(depthAttachmentRendererId);
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, rendererId);
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        specification.width = width;
        specification.height = height;
        RendererCommandAPI.setViewport(width, height);
        invalidate();
    }

    public void invalidate() {
        if (rendererId != 0) {
            delete();
        }

        rendererId = glCreateFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, rendererId);

        colorAttachmentRendererId = glCreateTextures(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, colorAttachmentRendererId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, specification.width, specification.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachmentRendererId, 0);

        depthAttachmentRendererId = glCreateTextures(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, depthAttachmentRendererId);
        glTexStorage2D(GL_TEXTURE_2D, 1, GL_DEPTH24_STENCIL8, specification.width, specification.height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthAttachmentRendererId, 0);

        YH_ASSERT(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE, "Framebuffer is incomplete!");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
