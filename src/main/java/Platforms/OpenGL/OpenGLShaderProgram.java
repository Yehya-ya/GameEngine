package Platforms.OpenGL;

import Engine.Renderer.ShaderProgram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static Engine.Utils.YH_Log.YH_ASSERT;
import static Engine.Utils.YH_Log.YH_LOG_ERROR;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class OpenGLShaderProgram extends ShaderProgram {
    public OpenGLShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
        super(vertexShaderFile, fragmentShaderFile);
    }

    public void compile() {
        String vertexShaderPath = "assets/shaders/vertex/" + vertexShaderFile + ".glsl";
        String source = "";
        try {
            source = Files.readString(Path.of(vertexShaderPath));
        } catch (IOException e) {
            YH_LOG_ERROR("Could not open the shader file: '{}'", vertexShaderFile);
            e.printStackTrace();
        }

        int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, source);
        glCompileShader(vertexShaderID);

        // print compile errors if any
        int success = glGetShaderi(vertexShaderID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            glDeleteShader(vertexShaderID);

            int len = glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH);
            YH_LOG_ERROR("Failed to compile the shader file: {}", vertexShaderFile);
            YH_LOG_ERROR("  {}", glGetShaderInfoLog(vertexShaderID, len));
            YH_ASSERT(false, "Failed to compile the shader file: " + vertexShaderFile);
        }

        String fragmentShaderPath = "assets/shaders/fragment/" + fragmentShaderFile + ".glsl";
        source = "";
        try {
            source = Files.readString(Path.of(fragmentShaderPath));
        } catch (IOException e) {
            YH_LOG_ERROR("Could not open the shader file: '{}'", fragmentShaderFile);
            e.printStackTrace();
        }

        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, source);
        glCompileShader(fragmentShaderID);

        // print compile errors if any
        success = glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            glDeleteShader(vertexShaderID);
            glDeleteShader(fragmentShaderID);

            int len = glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH);
            YH_LOG_ERROR("Failed to compile the shader file: {}", fragmentShaderFile);
            YH_LOG_ERROR("  {}", glGetShaderInfoLog(fragmentShaderID, len));
            YH_ASSERT(false, "Failed to compile the shader file: " + fragmentShaderFile);
        }

        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexShaderID);
        glAttachShader(shaderProgramID, fragmentShaderID);
        glLinkProgram(shaderProgramID);

        // print linking errors if any
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            glDeleteShader(vertexShaderID);
            glDeleteShader(fragmentShaderID);
            glDetachShader(shaderProgramID, vertexShaderID);
            glDetachShader(shaderProgramID, fragmentShaderID);

            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            YH_LOG_ERROR("Shader program failed to link vertex shader: {}, and the fragment shader: {}.", vertexShaderFile, fragmentShaderFile);
            YH_LOG_ERROR("  {}", glGetProgramInfoLog(shaderProgramID, len));
            YH_ASSERT(false, "Shader program failed to link vertex shader: " + vertexShaderFile + ", and the fragment shader: " + fragmentShaderFile + ".");
        }

        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDetachShader(shaderProgramID, vertexShaderID);
        glDetachShader(shaderProgramID, fragmentShaderID);

    }

    public void delete() {
        glDeleteProgram(shaderProgramID);
    }

    public void bind() {
        glUseProgram(shaderProgramID);
    }

    public void unbind() {
        glUseProgram(0);
    }
}
