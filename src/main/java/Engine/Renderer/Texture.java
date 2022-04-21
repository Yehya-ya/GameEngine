package Engine.Renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private final String filename;
    private int textureId;
    boolean pixelated;

    public Texture(String filename, boolean pixelated) {
        this.filename = filename;
        this.textureId = -1;
        this.pixelated = pixelated;
    }

    public void init() {
        String path = "assets/textures/" + filename;

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(path, width, height, channels, 0);

        if (image == null) {
            assert false : "Error: (Texture) Could not load image '" + path + "'";
            throw new RuntimeException("Error: (Texture) Could not load image '" + path + "'");
        }

        int textureFormat = GL_RGBA;
        int internalFormat = GL_RGBA32F;
        if (channels.get(0) == 3) {
            textureFormat = GL_RGB;
            internalFormat = GL_RGB32F;
        } else if (channels.get(0) != 4) {
            stbi_image_free(image);
            assert false : "Error: (Texture) Unknown number of channels '" + channels.get(0) + "'";
            throw new RuntimeException("Error: (Texture) Unknown number of channels '" + channels.get(0) + "'");
        }

        // Generate and bind the texture object
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, pixelated ? GL_NEAREST : GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, pixelated ? GL_NEAREST : GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width.get(0), height.get(0), 0, textureFormat, GL_UNSIGNED_BYTE, image);

        stbi_image_free(image);
    }

    public void bind(ShaderProgram shader) {
        check();
        int textureSlot = 0;
        glActiveTexture(GL_TEXTURE0 + textureSlot);
        glBindTexture(GL_TEXTURE_2D, textureId);
        shader.setInt("uTexture", textureSlot);
    }

    public void destroy() {
        check();
        glDeleteTextures(textureId);
    }

    private void check() {
        if (textureId == -1) {
            assert false : "Error: (Texture) texture not initialized '" + filename + "'";
            throw new RuntimeException("Error: (Texture) texture not initialized '" + filename + "'");
        }
    }
}
