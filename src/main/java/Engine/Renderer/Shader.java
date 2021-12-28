package Engine.Renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL45.*;

public class Shader {
    String filePath;
    // the program ID
    private int ID;
    private String vertexSource;
    private String fragmentSource;

    // constructor reads and builds the shader from a file path
    public Shader(String shaderFilePath) {
        filePath = shaderFilePath;
        shaderFilePath = "assets/shaders/" + shaderFilePath;
        try {
            String source = Files.readString(Path.of(shaderFilePath));
            String[] sourceSplits = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'pattern'
            final String tag = "#type";
            int index = source.indexOf(tag) + (tag.length() + 1);
            int eol = source.indexOf(System.lineSeparator(), index);
            String firstPattern = source.substring(index, eol).trim();

            // Find the second pattern after #type 'pattern'
            index = source.indexOf(tag, eol) + (tag.length() + 1);
            eol = source.indexOf(System.lineSeparator(), index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = sourceSplits[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = sourceSplits[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = sourceSplits[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = sourceSplits[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filePath + "'";
        }
    }

    public void compile() {
        int vertex, fragment;
        int success;

        // vertex Shader
        vertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex, vertexSource);
        glCompileShader(vertex);

        // print compile errors if any
        success = glGetShaderi(vertex, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertex, GL_INFO_LOG_LENGTH);
            System.out.println("Error in shader file: " + filePath + "\n");
            System.out.println("ERROR::SHADER::VERTEX::COMPILATION_FAILED\n");
            System.out.println("Vertex shader compilation failed.\n");
            glGetShaderInfoLog(vertex, len);
        }

        // Fragment Shader
        fragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment, fragmentSource);
        glCompileShader(fragment);

        // print compile errors if any
        success = glGetShaderi(fragment, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragment, GL_INFO_LOG_LENGTH);
            System.out.println("shader file: " + filePath + "\n");
            System.out.println("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n");
            System.out.println("fragment shader compilation failed.\n");
            glGetShaderInfoLog(fragment, len);
        }

        // shader Program
        ID = glCreateProgram();
        glAttachShader(ID, vertex);
        glAttachShader(ID, fragment);
        glLinkProgram(ID);
        // print linking errors if any
        success = glGetProgrami(ID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragment, GL_INFO_LOG_LENGTH);
            System.out.println("shader file: " + filePath + "\n");
            System.out.println("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n");
            System.out.println("fragment shader compilation failed.\n");
            glGetShaderInfoLog(fragment, len);
        }

        // delete the shaders as they're linked into our program now and no longer necessary
        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    // use/activate the shader
    void use() {
        glUseProgram(ID);
    }

    void detach() {
        glUseProgram(0);
    }

    // utility uniform functions
    void setBool(final String name, boolean value) {
        glUniform1i(glGetUniformLocation(ID, name), value ? 1 : 0);
    }

    void setInt(final String name, int value) {
        glUniform1i(glGetUniformLocation(ID, name), value);
    }

    void setFloat(final String name, float value) {
        glUniform1f(glGetUniformLocation(ID, name), value);
    }
}
