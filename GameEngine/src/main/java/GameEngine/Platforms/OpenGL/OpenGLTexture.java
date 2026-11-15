package GameEngine.Platforms.OpenGL;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.stb.STBImage.*;

import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Renderer.TextureLoadException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

public class OpenGLTexture extends Texture {
    private static final int internalFormat = GL_RGBA8;
    private static final int dataFormat = GL_RGBA;
    private static final int bytesPerPixel = 4;
    private final int textureId;

    public OpenGLTexture(int width, int height) {
        super(null);
        YH_LOG_TRACE("Creating empty texture.");

        this.width = width;
        this.height = height;

        textureId = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(textureId, 1, internalFormat, width, height);

        // Set texture parameters
        // Repeat image in both directions
        glTextureParameteri(textureId, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTextureParameteri(textureId, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTextureParameteri(textureId, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTextureParameteri(textureId, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    public OpenGLTexture(String path, byte @NotNull [] encodedImage) {
        super(path);
        YH_LOG_TRACE("Creating OpenGLTexture from \"{}\".", path);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer fileChannels = BufferUtils.createIntBuffer(1);
        ByteBuffer encodedImageBuffer = BufferUtils.createByteBuffer(encodedImage.length)
                .put(encodedImage)
                .flip();
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load_from_memory(encodedImageBuffer, width, height, fileChannels, STBI_rgb_alpha);
        if (image == null) {
            throw new TextureLoadException("Failed to load the image \"" + path + "\": " + stbi_failure_reason());
        }

        this.width = width.get(0);
        this.height = height.get(0);

        textureId = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(textureId, 1, internalFormat, this.width, this.height);

        // Set texture parameters
        // Repeat image in both directions
        glTextureParameteri(textureId, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTextureParameteri(textureId, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTextureParameteri(textureId, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTextureParameteri(textureId, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTextureSubImage2D(textureId, 0, 0, 0, this.width, this.height, dataFormat, GL_UNSIGNED_BYTE, image);

        stbi_image_free(image);
    }

    @Override
    public int getRendererId() {
        return textureId;
    }

    public void setData(@NotNull ByteBuffer data) {
        YH_ASSERT(data.remaining() == width * height * bytesPerPixel, "Data must be entire texture!");
        glTextureSubImage2D(textureId, 0, 0, 0, width, height, dataFormat, GL_UNSIGNED_BYTE, data);
    }

    @Override
    public void bind(int slot) {
        glBindTextureUnit(slot, textureId);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public void delete() {
        glDeleteTextures(textureId);
        YH_LOG_TRACE("Deleting OpenGLTexture.");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OpenGLTexture texture) {
            return textureId == texture.textureId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(textureId);
    }
}
