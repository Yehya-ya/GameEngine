package Engine.Renderer;

import Platforms.OpenGL.OpenGLShaderProgram;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.util.Map;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static Engine.Utils.YH_Log.YH_LOG_ERROR;

public abstract class ShaderProgram {
    protected String name;

    public static @NotNull ShaderProgram create(String path) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLShaderProgram(path);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public static @NotNull ShaderProgram create(String name, String vertexPath, String fragmentPath) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLShaderProgram(name, vertexPath, fragmentPath);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
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
