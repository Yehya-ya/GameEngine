package GameEngine.Engine.ECS;

import GameEngine.Engine.ECS.Components.CameraComponent;
import GameEngine.Engine.ECS.Components.SpriteComponent;
import GameEngine.Engine.ECS.Components.TagComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.Renderer.Camera.Camera;
import GameEngine.Engine.Renderer.Camera.CameraType;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;
import GameEngine.Engine.Renderer.Camera.PerspectiveCamera;
import GameEngine.Engine.Renderer.Texture;
import com.artemis.utils.IntBag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

public class SceneSerializer {

    private SceneSerializer() {
    }

    public static void serialize(@NotNull Scene scene, String path) {
        SceneData sceneData = new SceneData();
        sceneData.name = "untitled";
        IntBag intBag = scene.getEntitiesIds(TagComponent.class);
        for (int i = 0; i < intBag.size(); i++) {
            Entity entity = scene.getEntity(intBag.get(i));
            sceneData.entities.add(new EntityData(entity));
        }
        Yaml yaml = new Yaml();
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(yaml.dumpAsMap(sceneData));
            myWriter.close();
            YH_LOG_TRACE("Successfully serialized the scene.");
        } catch (IOException e) {
            YH_LOG_ERROR("failed to serialize the scene.\n{}", e.getMessage());
            e.printStackTrace();
        }
    }

    public static @Nullable Scene deserialize(String path) {
        InputStream targetStream;
        try {
            File initialFile = new File(path);
            targetStream = new FileInputStream(initialFile);
        } catch (FileNotFoundException | NullPointerException e) {
            YH_LOG_ERROR("failed to deserialize the scene.The file `{}` not found.", path);
            YH_LOG_ERROR("{}", e.getMessage());
            return null;
        }

        Yaml yaml = new Yaml();
        SceneData sceneData = yaml.loadAs(targetStream, SceneData.class);

        Scene scene = new Scene();
        for (EntityData entityData : sceneData.entities) {
            Entity entity = null;
            for (ComponentData componentData : entityData.components) {
                if (componentData instanceof TagComponentData tagComponentData) {
                    entity = scene.createEntity(tagComponentData.name);
                }

                if (entity == null) {
                    continue;
                }

                if (componentData instanceof TransformComponentData transformComponentData) {
                    entity.addComponent(new TransformComponent(transformComponentData.translate, transformComponentData.size, transformComponentData.rotation));
                }
                if (componentData instanceof CameraComponentData cameraComponentData) {
                    if (cameraComponentData.cameraData instanceof OrthographicCameraData orthographicCameraData) {
                        entity.addComponent(new CameraComponent(new OrthographicCamera(
                                orthographicCameraData.projectionMatrix,
                                orthographicCameraData.viewMatrix,
                                orthographicCameraData.viewProjectionMatrix,
                                orthographicCameraData.position,
                                orthographicCameraData.rotation,
                                orthographicCameraData.aspectRatio,
                                orthographicCameraData.far,
                                orthographicCameraData.near,
                                orthographicCameraData.zoomLevel
                        ), cameraComponentData.primary));
                    } else if (cameraComponentData.cameraData instanceof PerspectiveCameraData perspectiveCameraData) {
                        entity.addComponent(new CameraComponent(new PerspectiveCamera(
                                perspectiveCameraData.projectionMatrix,
                                perspectiveCameraData.viewMatrix,
                                perspectiveCameraData.viewProjectionMatrix,
                                perspectiveCameraData.position,
                                perspectiveCameraData.rotation,
                                perspectiveCameraData.aspectRatio,
                                perspectiveCameraData.far,
                                perspectiveCameraData.near,
                                perspectiveCameraData.fov
                        ), cameraComponentData.primary));
                    }
                } if (componentData instanceof SpriteComponentData spriteComponentData) {
                    if (spriteComponentData.texturePath == null) {
                        entity.addComponent(new SpriteComponent(spriteComponentData.color));
                    } else {
                        entity.addComponent(new SpriteComponent(Texture.create(spriteComponentData.texturePath), spriteComponentData.tilingFactor));
                    }
                }
            }
        }

        return scene;
    }

    public static class SceneData {
        public List<EntityData> entities;
        public String name;

        public SceneData() {
            entities = new ArrayList<>();
        }
    }

    public static class EntityData {
        public List<ComponentData> components;
        public int entity;

        public EntityData() {
            components = new ArrayList<>();
        }

        public EntityData(@NotNull Entity entity) {
            this.entity = entity.getId();
            components = new ArrayList<>();
            TagComponent tagComponent = entity.getComponent(TagComponent.class);
            if (tagComponent != null) {
                components.add(new TagComponentData(tagComponent));
            }

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if (transformComponent != null) {
                components.add(new TransformComponentData(transformComponent));
            }

            CameraComponent cameraComponent = entity.getComponent(CameraComponent.class);
            if (cameraComponent != null) {
                components.add(new CameraComponentData(cameraComponent));
            }

            SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
            if (spriteComponent != null) {
                components.add(new SpriteComponentData(spriteComponent));
            }
        }
    }

    public static abstract class ComponentData {

    }

    public static class TagComponentData extends ComponentData {
        public String name;

        public TagComponentData() {
        }

        public TagComponentData(@NotNull TagComponent tagComponent) {
            this.name = tagComponent.name;
        }
    }

    public static class TransformComponentData extends ComponentData {
        public Vector3f translate;
        public Vector3f size;
        public Vector3f rotation;

        public TransformComponentData() {
        }

        public TransformComponentData(@NotNull TransformComponent transformComponent) {
            this.translate = transformComponent.translate;
            this.size = transformComponent.size;
            this.rotation = transformComponent.rotation;
        }
    }

    public static class CameraComponentData extends ComponentData {
        public boolean primary;
        public CameraType cameraType;
        public CameraData cameraData;

        public CameraComponentData() {
        }

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

    public static class SpriteComponentData extends ComponentData {
        public Vector4f color;
        public float tilingFactor;
        public String texturePath;

        public SpriteComponentData() {
        }

        public SpriteComponentData(@NotNull SpriteComponent spriteComponent) {
            this.color = spriteComponent.color;
            this.tilingFactor = spriteComponent.tilingFactor;
            this.texturePath = (spriteComponent.texture == null) ? null : spriteComponent.texture.getPath();
        }
    }

    public abstract static class CameraData {
        public Matrix4f projectionMatrix;
        public Matrix4f viewMatrix;
        public Matrix4f viewProjectionMatrix;

        public Vector3f position;
        public Vector3f rotation;

        public float aspectRatio;
        public float far, near;

        public CameraData() {
        }

        public CameraData(@NotNull Camera camera) {
            this.projectionMatrix = camera.getProjectionMatrix();
            this.viewMatrix = camera.getViewMatrix();
            this.viewProjectionMatrix = camera.getViewProjectionMatrix();
            this.position = camera.getPosition();
            this.rotation = camera.getRotation();
            this.aspectRatio = camera.getAspectRatio();
            this.far = camera.getFar();
            this.near = camera.getNear();
        }
    }

    public static class OrthographicCameraData extends CameraData {
        public float zoomLevel;

        public OrthographicCameraData() {
            super();
        }

        public OrthographicCameraData(OrthographicCamera camera) {
            super(camera);
            this.zoomLevel = camera.getZoomLevel();
        }
    }

    public static class PerspectiveCameraData extends CameraData {
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
