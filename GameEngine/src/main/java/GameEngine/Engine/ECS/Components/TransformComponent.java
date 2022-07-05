package GameEngine.Engine.ECS.Components;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class TransformComponent extends com.artemis.Component {
    public Vector3f transform;
    public Vector2f size;
    public float rotationAngle;

    public TransformComponent(Vector3f transform, Vector2f size, float rotationAngle) {
        this.transform = transform;
        this.size = size;
        this.rotationAngle = rotationAngle;
    }

    public TransformComponent(Vector3f transform, Vector2f size) {
        this(transform, size, 0.0f);
    }

    public TransformComponent(Vector3f transform) {
        this(transform, new Vector2f(1.0f), 0.0f);
    }


    public TransformComponent() {
        this(new Vector3f(0.0f), new Vector2f(1.0f), 0.0f);
    }
}
