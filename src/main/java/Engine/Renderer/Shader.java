package Engine.Renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL40.*;

public class Shader {
    private final ShaderType mType;
    private final String mFilePath;
    private Integer mId;

    public Shader(String shaderFilePath, ShaderType type) {
        mFilePath = shaderFilePath;
        mType = type;
    }

    public int getId() {
        if (mId == null) {
            System.err.println("trying to get the shader id of none compiled shader\n");
            System.err.println(mType.name() + " shader: " + mFilePath + ".");
            this.compile();
        }
        return mId;
    }

    public void compile() {
        String source;
        String shaderFilePath = "assets/shaders/" + mFilePath + ".glsl";
        try {
            source = Files.readString(Path.of(shaderFilePath));
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + mFilePath + "'";
            return;
        }

        mId = glCreateShader(mType.glShaderTypeValue());
        glShaderSource(mId, source);
        glCompileShader(mId);

        // print compile errors if any
        int success = glGetShaderi(mId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(mId, GL_INFO_LOG_LENGTH);
            System.out.println("Error in shader file: " + mFilePath + "\n");
            System.out.println("ERROR::SHADER::" + mType.name() + "::COMPILATION_FAILED\n");
            System.out.println(glGetShaderInfoLog(mId, len));
            assert false : "shader " + mFilePath + " failed to compile.";
        }
    }

    public void detachAndDelete(int programId) {
        glDetachShader(programId, mId);
        glDeleteShader(mId);
    }
}
