package Engine.Renderer;

public abstract class ShaderProgram {
    protected final String fragmentShaderFile;
    protected final String vertexShaderFile;
    protected int shaderProgramID;

    public ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
        this.vertexShaderFile = vertexShaderFile;
        this.fragmentShaderFile = fragmentShaderFile;
        compile();
    }

    public abstract void compile();

    public abstract void destroy();

    public abstract void bind();

    public abstract void unbind();
}
