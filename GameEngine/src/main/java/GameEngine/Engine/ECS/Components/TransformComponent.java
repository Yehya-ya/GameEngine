package GameEngine.Engine.ECS.Components;

import org.joml.Math;
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

    public Vector3f getRotationInDegrees() {
        return new Vector3f((float) Math.toDegrees(rotation.x), (float) Math.toDegrees(rotation.y), (float) Math.toDegrees(rotation.z));
    }

    public void setRotationInDegrees(Vector3f rotation) {
        this.rotation = new Vector3f(Math.toRadians(rotation.x), Math.toRadians(rotation.y), Math.toRadians(rotation.z));
    }
}
