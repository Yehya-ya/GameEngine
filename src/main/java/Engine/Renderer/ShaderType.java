package Engine.Renderer;

import static org.lwjgl.opengl.GL40.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL40.GL_VERTEX_SHADER;

public enum ShaderType {
    VERTEX, FRAGMENT;

    public int glShaderTypeValue() {
        if (this == ShaderType.VERTEX) {
            return GL_VERTEX_SHADER;
        }
        return GL_FRAGMENT_SHADER;
    }
}
