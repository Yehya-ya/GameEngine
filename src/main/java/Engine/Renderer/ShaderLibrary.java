package Engine.Renderer;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static Engine.Utils.YH_Log.YH_ASSERT;

public class ShaderLibrary {
    Map<String, ShaderProgram> shaders;

    public ShaderLibrary() {
        shaders = new HashMap<>();
    }


    public void add(String name, ShaderProgram shaderProgram) {
        YH_ASSERT(!exist(name), "Shader'" + name + "' already exists!");
        shaders.put(name, shaderProgram);
    }

    public void add(ShaderProgram shaderProgram) {
        add(shaderProgram.getName(), shaderProgram);
    }

    public void remove(String name) {
        ShaderProgram shaderProgram = get(name);
        shaders.remove(name);
        shaderProgram.delete();
    }

    public void remove(@NotNull ShaderProgram shaderProgram) {
        remove(shaderProgram.getName());
    }

    public ShaderProgram load(String name, String path) {
        ShaderProgram shaderProgram = ShaderProgram.create(path);
        add(name, shaderProgram);
        return shaderProgram;
    }

    public ShaderProgram load(String path) {
        ShaderProgram shaderProgram = ShaderProgram.create(path);
        add(shaderProgram.getName(), shaderProgram);
        return shaderProgram;
    }


    public boolean exist(String name) {
        return shaders.containsKey(name);
    }

    public ShaderProgram get(String name) {
        YH_ASSERT(exist(name), "Shader '" + name + "' not found!.");
        return shaders.get(name);
    }
}
