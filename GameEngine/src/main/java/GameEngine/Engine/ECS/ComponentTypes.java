package GameEngine.Engine.ECS;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.ECS.Components.*;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Renderer.Camera.CameraType;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;
import GameEngine.Engine.Renderer.Camera.PerspectiveCamera;
import com.artemis.Component;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class ComponentTypes {
    public static final ComponentType<IdComponent, ?> Id = new ComponentType<>(
            "!IdComponent",
            IdComponent.class,
            IdComponentData.class,
            id -> new IdComponent(id.id),
            (id, project) -> new IdComponentData(id),
            (data, assets) -> new IdComponent(data.id));

    public static final ComponentType<TagComponent, ?> Tag = new ComponentType<>(
            "!TagComponent",
            TagComponent.class,
            TagComponentData.class,
            tag -> new TagComponent(tag.name),
            (tag, project) -> new TagComponentData(tag),
            (data, assets) -> new TagComponent(data.name));

    public static final ComponentType<TransformComponent, ?> Transform = new ComponentType<>(
            "!TransformComponent",
            TransformComponent.class,
            TransformComponentData.class,
            transform -> new TransformComponent(
                    new Vector3f(transform.translate), new Vector3f(transform.size), new Vector3f(transform.rotation)),
            (transform, project) -> new TransformComponentData(transform),
            (data, assets) -> new TransformComponent(data.translate, data.size, data.rotation));

    public static final ComponentType<CameraComponent, ?> Camera = new ComponentType<>(
            "!CameraComponent",
            CameraComponent.class,
            CameraComponentData.class,
            ComponentTypes::copyCamera,
            (camera, project) -> new CameraComponentData(camera),
            (data, assets) -> loadCamera(data));

    public static final ComponentType<SpriteComponent, ?> Sprite = new ComponentType<>(
            "!SpriteComponent",
            SpriteComponent.class,
            SpriteComponentData.class,
            SpriteComponent::new,
            SpriteComponentData::new,
            ComponentTypes::loadSprite);

    public static final List<ComponentType<?, ?>> All = List.of(Id, Tag, Transform, Camera, Sprite);

    public static final Map<Class<?>, String> NestedDataTags = Map.of(
            OrthographicCameraData.class, "!OrthographicCamera",
            PerspectiveCameraData.class, "!PerspectiveCamera");

    private ComponentTypes() {}

    public static @NotNull ComponentType<?, ?> of(@NotNull Component component) {
        for (ComponentType<?, ?> type : All) {
            if (type.componentClass() == component.getClass()) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                "No component type is registered for " + component.getClass().getSimpleName());
    }

    public static @NotNull ComponentType<?, ?> ofData(@NotNull Object data) {
        for (ComponentType<?, ?> type : All) {
            if (type.dataClass() == data.getClass()) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                "No component type saves its data as " + data.getClass().getSimpleName());
    }

    private static CameraComponent copyCamera(CameraComponent cameraComponent) {
        if (cameraComponent.camera instanceof OrthographicCamera orthographicCamera) {
            return new CameraComponent(
                    new OrthographicCamera(
                            new Vector3f(orthographicCamera.getPosition()),
                            new Vector3f(orthographicCamera.getRotation()),
                            orthographicCamera.getAspectRatio(),
                            orthographicCamera.getNear(),
                            orthographicCamera.getFar(),
                            orthographicCamera.getZoomLevel()),
                    cameraComponent.primary);
        }
        if (cameraComponent.camera instanceof PerspectiveCamera perspectiveCamera) {
            return new CameraComponent(
                    new PerspectiveCamera(
                            new Vector3f(perspectiveCamera.getPosition()),
                            new Vector3f(perspectiveCamera.getRotation()),
                            perspectiveCamera.getAspectRatio(),
                            perspectiveCamera.getNear(),
                            perspectiveCamera.getFar(),
                            perspectiveCamera.getFov()),
                    cameraComponent.primary);
        }
        throw new IllegalArgumentException("Cannot copy a camera component without a known camera");
    }

    private static CameraComponent loadCamera(CameraComponentData data) {
        if (data.cameraData instanceof OrthographicCameraData orthographicCameraData) {
            return new CameraComponent(
                    new OrthographicCamera(
                            orthographicCameraData.position,
                            orthographicCameraData.rotation,
                            orthographicCameraData.aspectRatio,
                            orthographicCameraData.near,
                            orthographicCameraData.far,
                            orthographicCameraData.zoomLevel),
                    data.primary);
        }
        if (data.cameraData instanceof PerspectiveCameraData perspectiveCameraData) {
            return new CameraComponent(
                    new PerspectiveCamera(
                            perspectiveCameraData.position,
                            perspectiveCameraData.rotation,
                            perspectiveCameraData.aspectRatio,
                            perspectiveCameraData.near,
                            perspectiveCameraData.far,
                            perspectiveCameraData.fov),
                    data.primary);
        }
        throw new IllegalArgumentException("A camera component has no camera");
    }

    private static SpriteComponent loadSprite(SpriteComponentData data, SceneAssets assets) {
        if (data.texturePath == null) {
            return new SpriteComponent(data.color);
        }
        return new SpriteComponent(assets.texture(data.texturePath), data.tilingFactor);
    }

    private static class IdComponentData {
        public UUID id;

        public IdComponentData() {}

        public IdComponentData(@NotNull IdComponent idComponent) {
            this.id = idComponent.id;
        }
    }

    private static class TagComponentData {
        public String name;

        public TagComponentData() {}

        public TagComponentData(@NotNull TagComponent tagComponent) {
            this.name = tagComponent.name;
        }
    }

    private static class TransformComponentData {
        public Vector3f translate;
        public Vector3f size;
        public Vector3f rotation;

        public TransformComponentData() {}

        public TransformComponentData(@NotNull TransformComponent transformComponent) {
            this.translate = transformComponent.translate;
            this.size = transformComponent.size;
            this.rotation = transformComponent.rotation;
        }
    }

    private static class CameraComponentData {
        public boolean primary;
        public CameraType cameraType;
        public CameraData cameraData;

        public CameraComponentData() {}

        public CameraComponentData(@NotNull CameraComponent cameraComponent) {
            this.primary = cameraComponent.primary;
            this.cameraType = cameraComponent.cameraType;
            if (cameraComponent.camera instanceof OrthographicCamera orthographicCamera) {
                this.cameraData = new OrthographicCameraData(orthographicCamera);
            } else if (cameraComponent.camera instanceof PerspectiveCamera perspectiveCamera) {
                this.cameraData = new PerspectiveCameraData(perspectiveCamera);
            }
        }
    }

    private static class SpriteComponentData {
        public Vector4f color;
        public float tilingFactor;
        public String texturePath;

        public SpriteComponentData() {}

        public SpriteComponentData(@NotNull SpriteComponent spriteComponent, @NotNull Project project) {
            boolean hasTextureFile = spriteComponent.showsTexture() && spriteComponent.texture.getPath() != null;
            this.color = hasTextureFile ? null : spriteComponent.shownColor();
            this.tilingFactor = spriteComponent.tilingFactor;
            this.texturePath =
                    hasTextureFile ? project.toProjectPath(Path.of(spriteComponent.texture.getPath())) : null;
        }
    }

    private abstract static class CameraData {
        public Vector3f position;
        public Vector3f rotation;

        public float aspectRatio;
        public float far, near;

        public CameraData() {}

        public CameraData(@NotNull Camera camera) {
            this.position = camera.getPosition();
            this.rotation = camera.getRotation();
            this.aspectRatio = camera.getAspectRatio();
            this.far = camera.getFar();
            this.near = camera.getNear();
        }
    }

    private static class OrthographicCameraData extends CameraData {
        public float zoomLevel;

        public OrthographicCameraData() {
            super();
        }

        public OrthographicCameraData(OrthographicCamera camera) {
            super(camera);
            this.zoomLevel = camera.getZoomLevel();
        }
    }

    private static class PerspectiveCameraData extends CameraData {
        public float fov;

        public PerspectiveCameraData() {
            super();
        }

        public PerspectiveCameraData(PerspectiveCamera camera) {
            super(camera);
            this.fov = camera.getFov();
        }
    }
}
