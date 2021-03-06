package GameEngine.Platforms.OpenGL;

import GameEngine.Engine.Renderer.ShaderProgram;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.joml.Math;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static GameEngine.Engine.Utils.YH_Log.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class OpenGLShaderProgram extends ShaderProgram {
    public static String TOKEN = "#type";
    private int programID;

    public OpenGLShaderProgram(@NotNull String path) {
        YH_LOG_TRACE("Creating OpenGLShaderProgram with one file \"{}\".", path);

        String source = ReadFile(path);
        Map<Integer, String> sourceMap = preProcess(source);
        compile(sourceMap);

        int lastSlash = Math.max(path.lastIndexOf('/'), 0);
        int lastBackSlash = Math.max(path.lastIndexOf('\\'), 0);
        int lastDot = path.lastIndexOf('.');
        this.name = path.substring(Math.max(lastBackSlash, lastSlash), lastDot);
    }

    public OpenGLShaderProgram(String name, String vertexShader, String fragmentShader) {
        YH_LOG_TRACE("Creating OpenGLShaderProgram with two files vertex shader: \"{}\" and fragment shader: \"{}\".", vertexShader, fragmentShader);

        Map<Integer, String> sourceMap = new HashMap<>();
        sourceMap.put(GL_VERTEX_SHADER, ReadFile(vertexShader));
        sourceMap.put(GL_FRAGMENT_SHADER, ReadFile(fragmentShader));
        compile(sourceMap);

        this.name = name;
    }

    @Override
    protected void compile(@NotNull Map<Integer, String> sourceMap) {
        List<Integer> ids = new ArrayList<>();
        for (Map.Entry<Integer, String> shader : sourceMap.entrySet()) {
            int id = glCreateShader(shader.getKey());
            glShaderSource(id, shader.getValue());
            glCompileShader(id);

            // print compile errors if any
            int success = glGetShaderi(id, GL_COMPILE_STATUS);
            if (success == GL_FALSE) {
                glDeleteShader(id);

                int len = glGetShaderi(id, GL_INFO_LOG_LENGTH);
                YH_LOG_ERROR("Failed to compile the shader.");
                YH_LOG_ERROR("  {}", glGetShaderInfoLog(id, len));
                YH_ASSERT(false, "Failed to compile the shader.");
                break;
            }
            ids.add(id);
        }

        programID = glCreateProgram();
        for (Integer id : ids)
            glAttachShader(programID, id);

        glLinkProgram(programID);

        // print linking errors if any
        int success = glGetProgrami(programID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            glDeleteProgram(programID);
            int len = glGetProgrami(programID, GL_INFO_LOG_LENGTH);
            YH_LOG_ERROR("Shader program failed to link shaders.");
            YH_LOG_ERROR("  {}", glGetProgramInfoLog(programID, len));
            YH_ASSERT(false, "Shader program failed to link shaders.");
        }

        for (Integer id : ids) {
            glDetachShader(programID, id);
            glDeleteShader(id);
        }
    }

    @Override
    public void delete() {
        glDeleteProgram(programID);
        YH_LOG_TRACE("Deleting OpenGLShaderProgram.");
    }

    @Override
    public void bind() {
        glUseProgram(programID);
    }

    @Override
    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public void UploadUniformInt(String name, int value) {
        int location = glGetUniformLocation(programID, name);
        glUniform1i(location, value);
    }

    @Override
    public void UploadUniformIntArray(String name, int[] value) {
        int location = glGetUniformLocation(programID, name);
        glUniform1iv(location, value);
    }

    @Override
    public void UploadUniformFloat(String name, float value) {
        int location = glGetUniformLocation(programID, name);
        glUniform1f(location, value);
    }

    @Override
    public void UploadUniformFloat2(String name, @NotNull Vector2f value) {
        int location = glGetUniformLocation(programID, name);
        glUniform2f(location, value.x, value.y);
    }

    @Override
    public void UploadUniformFloat3(String name, @NotNull Vector3f value) {
        int location = glGetUniformLocation(programID, name);
        glUniform3f(location, value.x, value.y, value.z);
    }

    @Override
    public void UploadUniformFloat4(String name, @NotNull Vector4f value) {
        int location = glGetUniformLocation(programID, name);
        glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    @Override
    public void UploadUniformMat3(String name, @NotNull Matrix3f matrix) {
        int location = glGetUniformLocation(programID, name);
        float[] arr = new float[9];
        matrix.get(arr);
        glUniformMatrix3fv(location, false, arr);
    }

    @Override
    public void UploadUniformMat4(String name, @NotNull Matrix4f matrix) {
        int location = glGetUniformLocation(programID, name);
        float[] arr = new float[16];
        matrix.get(arr);
        glUniformMatrix4fv(location, false, arr);
    }

    private @NotNull Map<Integer, String> preProcess(@NotNull String source) {
        Map<Integer, String> shaderSources = new HashMap<>();
        String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

        int begin = 0;
        for (int i = 1; i < splitString.length; i++) {
            begin = source.indexOf(TOKEN, begin);
            int end = source.indexOf("\r\n", begin);
            YH_ASSERT(end != -1, "Syntax error");
            String stringType = source.substring(begin + TOKEN.length(), end).trim();
            int type = shaderTypeFromString(stringType);
            YH_ASSERT(type != 0, "Invalid shader type specified");

            shaderSources.put(type, splitString[i]);
            begin = end;
        }
        return shaderSources;
    }

    private int shaderTypeFromString(@NotNull String type) {
        if (type.equals("vertex")) {
            return GL_VERTEX_SHADER;
        }
        if (type.equals("fragment") || type.equals("pixel")) {
            return GL_FRAGMENT_SHADER;
        }
        YH_ASSERT(false, "Unknown shader type!");
        return 0;
    }

    private String ReadFile(String path) {
        String source = "";
        try {
            source = Files.readString(Path.of(path));
        } catch (IOException e) {
            YH_LOG_ERROR("Could not open the shader file: '{}'", path);
            e.printStackTrace();
        }

        return source;
    }
}
