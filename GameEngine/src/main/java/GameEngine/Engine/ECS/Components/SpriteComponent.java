package GameEngine.Engine.ECS.Components;

import GameEngine.Engine.Renderer.Texture;
import org.joml.Vector4f;

public class SpriteComponent extends com.artemis.Component {
    public Vector4f color;
    public Texture texture;
    public float tilingFactor;
    public boolean isTextured;

    public SpriteComponent() {
        this(defaultColor());
    }

    public SpriteComponent(Vector4f color) {
        this.color = color;
        this.texture = null;
        this.tilingFactor = 1.0f;
        this.isTextured = false;
    }

    public SpriteComponent(Texture texture) {
        this(texture, 1.0f);
    }

    public SpriteComponent(Texture texture, float tilingFactor) {
        this.color = defaultColor();
        this.texture = texture;
        this.tilingFactor = tilingFactor;
        this.isTextured = true;
    }

    public SpriteComponent(SpriteComponent other) {
        this.color = new Vector4f(other.color);
        this.texture = other.texture;
        this.tilingFactor = other.tilingFactor;
        this.isTextured = other.isTextured;
    }

    public boolean showsTexture() {
        return isTextured && texture != null;
    }

    public Vector4f shownColor() {
        return isTextured ? new Vector4f(1.0f) : color;
    }

    public void setToColor() {
        this.isTextured = false;
        this.texture = null;
        this.tilingFactor = 1.0f;
    }

    public void setToTexture() {
        this.isTextured = true;
        this.texture = null;
        this.tilingFactor = 1.0f;
    }

    private static Vector4f defaultColor() {
        return new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
    }
}
