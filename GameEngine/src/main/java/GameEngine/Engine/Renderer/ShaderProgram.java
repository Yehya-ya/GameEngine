package GameEngine.Engine.Renderer;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

import GameEngine.Engine.Utils.Resources;
import GameEngine.Platforms.OpenGL.OpenGLShaderProgram;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

public abstract class ShaderProgram {
    protected String name;

    public static @NotNull ShaderProgram create(String path) {
        return fromSource(nameOf(path), readFile(path));
    }

    public static @NotNull ShaderProgram createFromResource(String resourceName) {
        return fromSource(nameOf(resourceName), Resources.readText(resourceName));
    }

    public static @NotNull ShaderProgram create(String name, String vertexPath, String fragmentPath) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLShaderProgram(name, readFile(vertexPath), readFile(fragmentPath));
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    private static @NotNull ShaderProgram fromSource(String name, String source) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLShaderProgram(name, source);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    private static @NotNull String nameOf(@NotNull String path) {
        int fileNameStart = Integer.max(path.lastIndexOf('/'), path.lastIndexOf('\\')) + 1;
        String fileName = path.substring(fileNameStart);
        int extensionStart = fileName.lastIndexOf('.');
        return extensionStart > 0 ? fileName.substring(0, extensionStart) : fileName;
    }

    private static @NotNull String readFile(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read the shader " + path, e);
        }
    }

    public String getName() {
        return name;
    }

    protected abstract void compile(Map<Integer, String> sourceMap);

    public abstract void delete();

    public abstract void bind();

    public abstract void unbind();

    public abstract void UploadUniformInt(String name, int value);

    public abstract void UploadUniformIntArray(String name, int[] value);

    public abstract void UploadUniformFloat(String name, float value);

    public abstract void UploadUniformFloat2(String name, Vector2f value);

    public abstract void UploadUniformFloat3(String name, Vector3f value);

    public abstract void UploadUniformFloat4(String name, Vector4f value);

    public abstract void UploadUniformMat3(String name, Matrix3f matrix);

    public abstract void UploadUniformMat4(String name, Matrix4f matrix);
}
