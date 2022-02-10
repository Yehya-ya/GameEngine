package Engine.Renderer;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL40.*;

public class ShaderProgram {
    private final String mVertexShaderFile;
    private final String mFragmentShaderFile;
    private final Map<String, Integer> uniforms;
    private int mId;

    public ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
        mVertexShaderFile = vertexShaderFile;
        mFragmentShaderFile = fragmentShaderFile;
        uniforms = new HashMap<>();
    }

    // use/activate the shader
    void use() {
        glUseProgram(mId);
    }

    void detach() {
        glUseProgram(0);
    }

    void compileAndLink() {
        // create and compile shaders
        Shader vertexShader = new Shader(mVertexShaderFile, ShaderType.VERTEX);
        Shader fragmentShader = new Shader(mFragmentShaderFile, ShaderType.FRAGMENT);
        vertexShader.compile();
        fragmentShader.compile();

        // create shader Program
        mId = glCreateProgram();
        glAttachShader(mId, vertexShader.getId());
        glAttachShader(mId, fragmentShader.getId());
        glLinkProgram(mId);

        // print linking errors if any
        int success = glGetProgrami(mId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(mId, GL_INFO_LOG_LENGTH);
            System.err.println("ERROR::SHADER_PROGRAM::LINKING_FAILED\n");
            System.err.println("shader program failed to link " + mVertexShaderFile + " and " + mFragmentShaderFile + ".\n");
            System.err.println(glGetProgramInfoLog(mId, len));
            return;
        }

        // get all active uniforms in the program
        int numUniforms = glGetProgrami(mId, GL_ACTIVE_UNIFORMS);
        int maxCharLength = glGetProgrami(mId, GL_ACTIVE_UNIFORM_MAX_LENGTH);

        for (int i = 0; i < numUniforms; i++) {
            IntBuffer size = BufferUtils.createIntBuffer(1);
            IntBuffer type = BufferUtils.createIntBuffer(1);
            String name = glGetActiveUniform(mId, i, maxCharLength, size, type);
            uniforms.put(name, i);
        }

        // detach and delete the shaders as they're linked into our program now and no longer necessary
        vertexShader.detachAndDelete(mId);
        fragmentShader.detachAndDelete(mId);
    }

    // utility uniform functions
    void setBool(final String name, boolean value) {
        Integer location = uniforms.get(name);
        if (location == null) {
            System.err.println("there is no uniform with name :" + name);
            return;
        }
        glUniform1i(location, value ? 1 : 0);
    }

    void setInt(final String name, int value) {
        Integer location = uniforms.get(name);
        if (location == null) {
            System.err.println("there is no uniform with name :" + name);
            return;
        }
        glUniform1i(location, value);
    }

    void setFloat(final String name, float value) {
        Integer location = uniforms.get(name);
        if (location == null) {
            System.err.println("there is no uniform with name :" + name);
            return;
        }
        glUniform1f(location, value);
    }
}
