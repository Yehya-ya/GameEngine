package GameEngine.Engine.ECS.Components;

import org.joml.Vector3f;

public class TransformComponent extends com.artemis.Component {
    public Vector3f translate;
    public Vector3f size;
    public Vector3f rotation;

    public TransformComponent(Vector3f translate, Vector3f size, Vector3f rotation) {
        this.translate = translate;
        this.size = size;
        this.rotation = rotation;
    }

    public TransformComponent(Vector3f translate, Vector3f size) {
        this(translate, size, new Vector3f());
    }

    public TransformComponent(Vector3f translate) {
        this(translate, new Vector3f(1.0f), new Vector3f());
    }

    public TransformComponent() {
        this(new Vector3f(), new Vector3f(1.0f), new Vector3f());
    }

    public Vector3f getRotationInRadians() {
        return new Vector3f((float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), (float) Math.toRadians(rotation.z));
    }
}
