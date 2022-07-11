package GameEngine.Engine.ECS.Components;

import org.joml.Vector3f;

public class TransformComponent extends com.artemis.Component {
    public Vector3f transform;
    public Vector3f size;
    public Vector3f rotation;

    public TransformComponent(Vector3f transform, Vector3f size, Vector3f rotation) {
        this.transform = transform;
        this.size = size;
        this.rotation = rotation;
    }

    public TransformComponent(Vector3f transform, Vector3f size) {
        this(transform, size, new Vector3f());
    }

    public TransformComponent(Vector3f transform) {
        this(transform, new Vector3f(1.0f), new Vector3f());
    }

    public TransformComponent() {
        this(new Vector3f(0.0f), new Vector3f(1.0f), new Vector3f());
    }

    public Vector3f getRotationInRadians() {
        return new Vector3f((float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), (float) Math.toRadians(rotation.z));
    }
}
