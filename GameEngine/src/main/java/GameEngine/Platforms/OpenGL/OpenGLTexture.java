package GameEngine.Platforms.OpenGL;

import GameEngine.Engine.Renderer.Texture;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.stb.STBImage.*;

public class OpenGLTexture extends Texture {
    private final int textureId;
    private final int internalFormat;
    private final int dataFormat;

    public OpenGLTexture(int width, int height) {
        super(null);
        YH_LOG_TRACE("Creating empty texture.");

        this.width = width;
        this.height = height;

        internalFormat = GL_RGB8;
        dataFormat = GL_RGB;
        textureId = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(textureId, 1, internalFormat, width, height);

        // Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    public OpenGLTexture(String path) {
        super(path);
        YH_LOG_TRACE("Creating OpenGLTexture from file path: \"{}\".", path);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(path, width, height, channels, 0);

        YH_ASSERT(image != null, "Failed to load image!");

        this.width = width.get(0);
        this.height = height.get(0);

        int internalFormat = 0, dataFormat = 0;
        if (channels.get(0) == 3) {
            internalFormat = GL_RGB8;
            dataFormat = GL_RGB;
        } else if (channels.get(0) == 4) {
            internalFormat = GL_RGBA8;
            dataFormat = GL_RGBA;
        }

        YH_ASSERT((internalFormat & dataFormat) != 0, "Error: (Texture) Unknown number of channels '" + channels.get(0) + "'");

        this.internalFormat = internalFormat;
        this.dataFormat = dataFormat;

        textureId = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(textureId, 1, internalFormat, this.width, this.height);

        // Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTextureSubImage2D(textureId, 0, 0, 0, this.width, this.height, dataFormat, GL_UNSIGNED_BYTE, image);

        stbi_image_free(image);
    }

    @Override
    public int getRendererId() {
        return textureId;
    }

    public void setData(@NotNull ByteBuffer data) {
        int depth = (dataFormat == GL_RGBA) ? 4 : 3;
        YH_ASSERT(data.remaining() == width * height * depth, "Data must be entire texture!");
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
}
