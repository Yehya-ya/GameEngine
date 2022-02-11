package Engine.Renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL40.*;

public class Shader {
    private final ShaderType type;
    private final String filePath;
    private Integer id;

    public Shader(String shaderFilePath, ShaderType type) {
        filePath = shaderFilePath;
        this.type = type;
    }

    public int getId() {
        if (id == null) {
            System.err.println("trying to get the shader id of none compiled shader\n");
            System.err.println(type.name() + " shader: " + filePath + ".");
            assert false : "trying to get the shader id of none compiled shader\n";
            return -1;
        }
        return id;
    }

    public boolean compile() {
        String source;
        String shaderFilePath = "assets/shaders/" + type.name().toLowerCase() + "/" + filePath + ".glsl";
        try {
            source = Files.readString(Path.of(shaderFilePath));
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filePath + "'";
            return false;
        }

        id = glCreateShader(type.value);
        glShaderSource(id, source);
        glCompileShader(id);

        // print compile errors if any
        int success = glGetShaderi(id, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(id, GL_INFO_LOG_LENGTH);
            System.out.println("Error in shader file: " + filePath + "\n");
            System.out.println("ERROR::SHADER::" + type.name() + "::COMPILATION_FAILED\n");
            System.out.println(glGetShaderInfoLog(id, len));
            assert false : "shader " + filePath + " failed to compile.";
            return false;
        }
        return true;
    }

    public void detachAndDelete(int programId) {
        glDetachShader(programId, id);
        glDeleteShader(id);
    }

    public enum ShaderType {
        VERTEX(GL_VERTEX_SHADER), FRAGMENT(GL_FRAGMENT_SHADER);
        public final int value;

        ShaderType(int value) {
            this.value = value;
        }
    }
}
