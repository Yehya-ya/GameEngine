package Engine.Renderer;

import Engine.Scenes.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Cube extends ObjectArrayBuffer{
    private Vector3f pos;

    public Cube(Scene scene, Vector3f pos) {
        super(scene, "vertexShader3D", "textureFragmentShader", "cube", "bricks.png");
        this.pos = pos;
        this.transformationMatrix = new Matrix4f().translate(pos).scale(new Vector3f(0.5f));
    }

    @Override
    public void update(float dt) {

    }
}
