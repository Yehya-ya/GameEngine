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
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

public class SceneSerializer {

    private SceneSerializer() {
    }

    public static void serialize(@NotNull Scene scene, String path) {
        scene.setUri(path);
        SceneData sceneData = new SceneData();
        sceneData.title = scene.getTitle();
        IntBag intBag = scene.getEntitiesIds(TagComponent.class);
        for (int i = 0; i < intBag.size(); i++) {
            Entity entity = scene.getEntity(intBag.get(i));
            sceneData.entities.add(new EntityData(entity));
        }

        Representer representer = new SceneDataRepresenter();
        Yaml yaml = new Yaml(representer);
        try {
            FileWriter myWriter = new FileWriter(path);
            yaml.dump(sceneData, myWriter);
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
            targetStream = new FileInputStream(path);
        } catch (FileNotFoundException | NullPointerException e) {
            YH_LOG_ERROR("failed to deserialize the scene.The file `{}` not found.", path);
            YH_LOG_ERROR("{}", e.getMessage());
            return null;
        }

        Yaml yaml = new Yaml(new SceneDataConstructor());
        SceneData sceneData = yaml.load(targetStream);

        Scene scene = new Scene(sceneData.title, path);
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
                        entity.addComponent(new CameraComponent(new OrthographicCamera(orthographicCameraData.projectionMatrix, orthographicCameraData.viewMatrix, orthographicCameraData.viewProjectionMatrix, orthographicCameraData.position, orthographicCameraData.rotation, orthographicCameraData.aspectRatio, orthographicCameraData.near, orthographicCameraData.far, orthographicCameraData.zoomLevel), cameraComponentData.primary));
                    } else if (cameraComponentData.cameraData instanceof PerspectiveCameraData perspectiveCameraData) {
                        entity.addComponent(new CameraComponent(new PerspectiveCamera(perspectiveCameraData.projectionMatrix, perspectiveCameraData.viewMatrix, perspectiveCameraData.viewProjectionMatrix, perspectiveCameraData.position, perspectiveCameraData.rotation, perspectiveCameraData.aspectRatio, perspectiveCameraData.near, perspectiveCameraData.far, perspectiveCameraData.fov), cameraComponentData.primary));
                    }
                }
                if (componentData instanceof SpriteComponentData spriteComponentData) {
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

    private static class SceneData {
        public List<EntityData> entities;
        public String title;

        public SceneData() {
            entities = new ArrayList<>();
        }
    }

    private static class EntityData {
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

    private static abstract class ComponentData {

    }

    private static class TagComponentData extends ComponentData {
        public String name;

        public TagComponentData() {
        }

        public TagComponentData(@NotNull TagComponent tagComponent) {
            this.name = tagComponent.name;
        }
    }

    private static class TransformComponentData extends ComponentData {
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

    private static class CameraComponentData extends ComponentData {
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

    private static class SpriteComponentData extends ComponentData {
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

    private abstract static class CameraData {
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

    private static class SceneDataRepresenter extends Representer {
        public SceneDataRepresenter() {
            super(new DumperOptions());

            this.addClassTag(SceneData.class, new Tag("!Scene"));
            this.addClassTag(TagComponentData.class, new Tag("!TagComponent"));
            this.addClassTag(TransformComponentData.class, new Tag("!TransformComponent"));
            this.addClassTag(CameraComponentData.class, new Tag("!CameraComponent"));
            this.addClassTag(SpriteComponentData.class, new Tag("!SpriteComponent"));
            this.addClassTag(OrthographicCameraData.class, new Tag("!OrthographicCamera"));
            this.addClassTag(PerspectiveCameraData.class,new Tag("!PerspectiveCamera"));
        }
    }

    private static class SceneDataConstructor extends Constructor {
        public SceneDataConstructor() {
            super(new LoaderOptions());

            this.addTypeDescription(new TypeDescription(SceneData.class,"!Scene"));
            this.addTypeDescription(new TypeDescription(TagComponentData.class,"!TagComponent"));
            this.addTypeDescription(new TypeDescription(TransformComponentData.class,"!TransformComponent"));
            this.addTypeDescription(new TypeDescription(CameraComponentData.class,"!CameraComponent"));
            this.addTypeDescription(new TypeDescription(SpriteComponentData.class,"!SpriteComponent"));
            this.addTypeDescription(new TypeDescription(OrthographicCameraData.class,"!OrthographicCamera"));
            this.addTypeDescription(new TypeDescription(PerspectiveCameraData.class,"!PerspectiveCamera"));
        }
    }
}
