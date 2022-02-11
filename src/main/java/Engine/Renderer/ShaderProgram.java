package Engine.Renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL40.*;

public class ShaderProgram {
    private final String vertexShaderFile;
    private final String fragmentShaderFile;
    private final Map<String, Integer> uniforms;
    private int id;

    public ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
        this.vertexShaderFile = vertexShaderFile;
        this.fragmentShaderFile = fragmentShaderFile;
        uniforms = new HashMap<>();
    }

    public boolean compileAndLink() {
        // create and compile shaders
        Shader vertexShader = new Shader(vertexShaderFile, Shader.ShaderType.VERTEX);
        Shader fragmentShader = new Shader(fragmentShaderFile, Shader.ShaderType.FRAGMENT);
        if (!vertexShader.compile() || !fragmentShader.compile()) {
            return false;
        }

        // create shader Program
        id = glCreateProgram();
        glAttachShader(id, vertexShader.getId());
        glAttachShader(id, fragmentShader.getId());
        glLinkProgram(id);

        // print linking errors if any
        int success = glGetProgrami(id, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(id, GL_INFO_LOG_LENGTH);
            System.err.println("ERROR::SHADER_PROGRAM::LINKING_FAILED\n");
            System.err.println("shader program failed to link " + vertexShaderFile + " and " + fragmentShaderFile + ".\n");
            System.err.println(glGetProgramInfoLog(id, len));
            return false;
        }

        // get all active uniforms in the program
        int numUniforms = glGetProgrami(id, GL_ACTIVE_UNIFORMS);
        int maxCharLength = glGetProgrami(id, GL_ACTIVE_UNIFORM_MAX_LENGTH);

        for (int i = 0; i < numUniforms; i++) {
            IntBuffer size = BufferUtils.createIntBuffer(1);
            IntBuffer type = BufferUtils.createIntBuffer(1);
            String name = glGetActiveUniform(id, i, maxCharLength, size, type);
            uniforms.put(name, i);
        }

        // detach and delete the shaders as they're linked into our program now and no longer necessary
        vertexShader.detachAndDelete(id);
        fragmentShader.detachAndDelete(id);
        return  true;
    }

    public void bind() {
        glUseProgram(id);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void destroy() {
        glDeleteProgram(id);
    }

    // utility uniform functions
    public void setBool(final String name, boolean value) {
        int location = getUniformLocation(name);
        glUniform1i(location, value ? 1 : 0);
    }

    public void setInt(final String name, int value) {
        int location = getUniformLocation(name);
        glUniform1i(location, value);
    }

    public void setFloat(final String name, float value) {
        int location = getUniformLocation(name);
        glUniform1f(location, value);
    }

    public void setMatrix3f(final String name, Matrix3f matrix3f) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = matrix3f.get(stack.mallocFloat(9));
            glUniformMatrix3fv(location, false, buffer);
        }
    }

    public void setMatrix4f(final String name, Matrix4f matrix4f) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = matrix4f.get(stack.mallocFloat(16));
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void setVector2f(final String name, Vector2f vector2f) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = vector2f.get(stack.mallocFloat(2));
            glUniform2fv(location, buffer);
        }
    }

    public void setVector3f(final String name, Vector3f vector3f) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = vector3f.get(stack.mallocFloat(3));
            glUniform3fv(location, buffer);
        }
    }

    public void setVector4f(final String name, Vector4f vector4f) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = vector4f.get(stack.mallocFloat(4));
            glUniform4fv(location, buffer);
        }
    }

    private int getUniformLocation(String name) {
        Integer location = uniforms.get(name);
        if (location == null) {
            System.err.println("the " + name +" uniform is not an active uniform in the shader program");
            assert false : "the " + name +" uniform is not an active uniform in the shader program";
            return -1;
        }
        return location;
    }


}
