package GameEngine.Engine.Renderer;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

import GameEngine.Engine.Utils.Resources;
import GameEngine.Platforms.OpenGL.OpenGLShaderProgram;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

public abstract class ShaderProgram {
    protected String name;

    public static @NotNull ShaderProgram createFromResource(String resourceName) {
        return fromSource(nameOf(resourceName), Resources.readText(resourceName));
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

    public String getName() {
        return name;
    }

    protected abstract void compile(Map<Integer, String> sourceMap);

    public abstract void delete();

    public abstract void bind();

    public abstract void unbind();

    public abstract void uploadUniformInt(String name, int value);

    public abstract void uploadUniformIntArray(String name, int[] value);

    public abstract void uploadUniformFloat(String name, float value);

    public abstract void uploadUniformFloat2(String name, Vector2f value);

    public abstract void uploadUniformFloat3(String name, Vector3f value);

    public abstract void uploadUniformFloat4(String name, Vector4f value);

    public abstract void uploadUniformMat3(String name, Matrix3f matrix);

    public abstract void uploadUniformMat4(String name, Matrix4f matrix);
}
