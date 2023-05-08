package GameEngine.Platforms.OpenGL;

import GameEngine.Engine.Renderer.Buffer.FrameBuffer;
import GameEngine.Engine.Renderer.RendererCommandAPI;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_WARN;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL45.*;

public class OpenGLFrameBuffer extends FrameBuffer {
    public static final int MaxFramebufferSize = 8192;
    private final List<FramebufferTextureSpecification> colorAttachmentSpecifications;
    private final FrameBufferSpecification specification;
    private FramebufferTextureSpecification depthAttachmentSpecification = new FramebufferTextureSpecification();
    private int[] colorAttachments;
    private int depthAttachment = 0;
    private int rendererId, depthAttachmentRendererId;

    public OpenGLFrameBuffer(FrameBufferSpecification specification) {
        this.specification = specification;
        this.colorAttachmentSpecifications = new ArrayList<>();
        this.colorAttachments = new int[0];
        for (FramebufferTextureSpecification spec : specification.attachments.attachments) {
            if (!Utils.isDepthFormat(spec.textureFormat)) colorAttachmentSpecifications.add(spec);
            else depthAttachmentSpecification = spec;
        }
        invalidate();
    }

    @Override
    public FrameBufferSpecification getSpecification() {
        return specification;
    }

    @Override
    public int getColorAttachmentRendererId(int index) {
        YH_ASSERT(index < colorAttachments.length, "index out of bounds for color attachments array");
        return colorAttachments[index];
    }

    @Override
    public void delete() {
        glDeleteFramebuffers(rendererId);
        glDeleteTextures(colorAttachments);
        glDeleteTextures(depthAttachmentRendererId);
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, rendererId);
        RendererCommandAPI.setViewport(specification.width, specification.height);
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0 || width >= MaxFramebufferSize || height >= MaxFramebufferSize) {
            YH_LOG_WARN("Attempted to resize framebuffer to {}, {}", width, height);
            return;
        }
        specification.width = width;
        specification.height = height;
        invalidate();
    }

    @Override
    public int ReadPixel(int attachmentIndex, int x, int y) {
        YH_ASSERT(attachmentIndex < colorAttachments.length, "index out of bounds for color attachments array");

        glReadBuffer(GL_COLOR_ATTACHMENT0 + attachmentIndex);
        int[] pixelData = new int[1];
        glReadPixels(x, y, 1, 1, GL_RED_INTEGER, GL_INT, pixelData);
        return pixelData[0];
    }

    @Override
    public void clearAttachment(int attachmentIndex, int value) {
        YH_ASSERT(attachmentIndex < colorAttachments.length, "index out of bounds for color attachments array");

        FramebufferTextureSpecification spec = colorAttachmentSpecifications.get(attachmentIndex);
        int[] valuesBuffer = new int[]{value};
        glClearTexImage(colorAttachments[attachmentIndex], 0, Utils.FBTextureFormatToGL(spec.textureFormat), GL_INT, valuesBuffer);
    }

    public void invalidate() {
        if (rendererId != 0) {
            delete();

            colorAttachments = new int[0];
            depthAttachment = 0;
        }

        rendererId = glCreateFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, rendererId);

        boolean multisample = specification.samples > 1;

        // Attachments
        if (colorAttachmentSpecifications.size() > 0) {
            colorAttachments = Utils.createTextures(multisample, colorAttachmentSpecifications.size());

            for (int i = 0; i < colorAttachments.length; i++) {
                Utils.bindTexture(multisample, colorAttachments[i]);
                switch (colorAttachmentSpecifications.get(i).textureFormat) {
                    case RGBA8 -> {
                        Utils.attachColorTexture(colorAttachments[i], specification.samples, GL_RGBA8, GL_RGBA, specification.width, specification.height, i);
                    }
                    case RED_INTEGER -> {
                        Utils.attachColorTexture(colorAttachments[i], specification.samples, GL_R32I, GL_RED_INTEGER, specification.width, specification.height, i);
                    }
                }
            }
        }

        if (depthAttachmentSpecification.textureFormat != FramebufferTextureFormat.None) {
            depthAttachment = Utils.createTextures(multisample, 1)[0];
            Utils.bindTexture(multisample, depthAttachment);
            if (Objects.requireNonNull(depthAttachmentSpecification.textureFormat) == FramebufferTextureFormat.DEPTH24STENCIL8) {
                Utils.attachDepthTexture(depthAttachment, specification.samples, GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL_ATTACHMENT, specification.width, specification.height);
            }
        }

        if (colorAttachments.length > 1) {
            YH_ASSERT(colorAttachments.length <= 4, "colorAttachments length grater than 4");
            int[] buffers = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3};
            glDrawBuffers(buffers);
        } else if (colorAttachments.length == 0) {
            // Only depth-pass
            glDrawBuffer(GL_NONE);
        }

        YH_ASSERT(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE, "Framebuffer is incomplete!");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private static class Utils {
        static int TextureTarget(boolean multisampled) {
            return multisampled ? GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D;
        }

        static int[] createTextures(boolean multisampled, int count) {
            int[] out = new int[count];
            glCreateTextures(TextureTarget(multisampled), out);
            return out;
        }

        static void bindTexture(boolean multisampled, int id) {
            glBindTexture(TextureTarget(multisampled), id);
        }

        static void attachColorTexture(int id, int samples, int internalFormat, int format, int width, int height, int index) {
            boolean multisampled = samples > 1;
            if (multisampled) {
                glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, internalFormat, width, height, false);
            } else {
                glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, (ByteBuffer) null);

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            }

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + index, TextureTarget(multisampled), id, 0);
        }

        static void attachDepthTexture(int id, int samples, int format, int attachmentType, int width, int height) {
            boolean multisampled = samples > 1;
            if (multisampled) {
                glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, format, width, height, false);
            } else {
                glTexStorage2D(GL_TEXTURE_2D, 1, format, width, height);

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            }

            glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, TextureTarget(multisampled), id, 0);
        }

        static boolean isDepthFormat(FramebufferTextureFormat format) {
            return Objects.requireNonNull(format) == FramebufferTextureFormat.DEPTH24STENCIL8;
        }

        static int FBTextureFormatToGL(FramebufferTextureFormat format) {
            switch (format) {
                case RGBA8 -> {
                    return GL_RGBA8;
                }
                case RED_INTEGER -> {
                    return GL_RED_INTEGER;
                }
            }

            YH_ASSERT(false, "invalid format");
            return 0;
        }
    }
}
