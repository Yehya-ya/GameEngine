package Engine.Renderer;

import Platforms.OpenGL.OpenGLShaderProgram;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static Engine.Utils.YH_Log.YH_LOG_ERROR;

public abstract class ShaderProgram {
    protected final String fragmentShaderFile;
    protected final String vertexShaderFile;
    protected int shaderProgramID;

    protected ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
        this.vertexShaderFile = vertexShaderFile;
        this.fragmentShaderFile = fragmentShaderFile;
        compile();
    }

    public static ShaderProgram create(String vertexShaderFile, String fragmentShaderFile) {
        switch (RendererCommandAPI.getApi()) {
            case OpenGL -> {
                return new OpenGLShaderProgram(vertexShaderFile, fragmentShaderFile);
            }
        }

        YH_LOG_ERROR("unknown renderer type {}", RendererCommandAPI.getApi().name());
        YH_ASSERT(false, "unknown renderer type");
        return null;
    }

    public abstract void compile();

    public abstract void delete();

    public abstract void bind();

    public abstract void unbind();
}
