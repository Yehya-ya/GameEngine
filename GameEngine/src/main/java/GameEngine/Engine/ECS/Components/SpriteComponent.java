package GameEngine.Engine.ECS.Components;

import GameEngine.Engine.Renderer.Texture;
import org.joml.Vector4f;

public class SpriteComponent extends com.artemis.Component {
    public Vector4f color;
    public Texture texture;
    public float tilingFactor;

    public SpriteComponent() {
        this(new Vector4f(0.8f,0.8f,0.8f,1.0f));
    }

    public SpriteComponent(Vector4f color) {
        this.color = color;
        this.texture = null;
    }

    public SpriteComponent(Texture texture) {
        this(texture, 1.0f);
    }

    public SpriteComponent(Texture texture, float tilingFactor) {
        this.texture = texture;
        this.tilingFactor = tilingFactor;
        this.color = null;
    }
}
