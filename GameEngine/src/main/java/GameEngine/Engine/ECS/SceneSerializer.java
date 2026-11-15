package GameEngine.Engine.ECS;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;
import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.ECS.Components.TagComponent;
import GameEngine.Engine.Renderer.TextureLoadException;
import com.artemis.Component;
import com.artemis.utils.IntBag;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class SceneSerializer {

    private SceneSerializer() {}

    public static boolean serialize(@NotNull Scene scene, String path, @NotNull Project project) {
        try {
            Files.writeString(Path.of(path), toYaml(scene, project));
        } catch (IOException | InvalidPathException e) {
            YH_LOG_ERROR("failed to serialize the scene to `{}`.\n{}", path, e.getMessage());
            return false;
        }
        scene.setUri(path);
        YH_LOG_TRACE("Successfully serialized the scene.");
        return true;
    }

    public static @NotNull String toYaml(@NotNull Scene scene, @NotNull Project project) {
        SceneData sceneData = new SceneData();
        sceneData.title = scene.getTitle();
        IntBag intBag = scene.getEntitiesIds(TagComponent.class);
        for (int i = 0; i < intBag.size(); i++) {
            Entity entity = scene.getEntity(intBag.get(i));
            sceneData.entities.add(new EntityData(entity, project));
        }
        return new Yaml(new SceneDataRepresenter(), new DumperOptions()).dump(sceneData);
    }

    public static @Nullable Scene deserialize(String path, @NotNull Project project) {
        SceneData sceneData;
        try (InputStream targetStream = new FileInputStream(path)) {
            sceneData = new Yaml(new SceneDataConstructor()).load(targetStream);
        } catch (FileNotFoundException | NullPointerException e) {
            YH_LOG_ERROR("failed to deserialize the scene.The file `{}` not found.", path);
            YH_LOG_ERROR("{}", e.getMessage());
            return null;
        } catch (IOException | YAMLException e) {
            YH_LOG_ERROR("failed to deserialize the scene. The file `{}` is not a valid scene.", path);
            YH_LOG_ERROR("{}", e.getMessage());
            return null;
        }

        if (sceneData == null) {
            YH_LOG_ERROR("failed to deserialize the scene. The file `{}` is empty.", path);
            return null;
        }

        Scene scene = new Scene(sceneData.title, path);
        SceneAssets assets = new SceneAssets(project, scene.getTextures());
        try {
            for (EntityData entityData : sceneData.entities) {
                Entity entity = scene.emptyEntity();
                for (Object componentData : entityData.components) {
                    entity.addComponent(ComponentTypes.ofData(componentData).load(componentData, assets));
                }
            }
        } catch (IllegalArgumentException | TextureLoadException e) {
            YH_LOG_ERROR("failed to deserialize the scene `{}`.", path);
            YH_LOG_ERROR("{}", e.getMessage());
            scene.delete();
            return null;
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
        public List<Object> components;
        public int entity;

        public EntityData() {
            components = new ArrayList<>();
        }

        public EntityData(@NotNull Entity entity, @NotNull Project project) {
            this.entity = entity.getId();
            components = new ArrayList<>();
            for (ComponentType<?, ?> type : ComponentTypes.All) {
                Component component = entity.getComponent(type.componentClass());
                if (component != null) {
                    components.add(type.save(component, project));
                }
            }
        }
    }

    private static class SceneDataRepresenter extends Representer {
        public SceneDataRepresenter() {
            super(new DumperOptions());

            this.addClassTag(SceneData.class, new Tag("!Scene"));
            for (ComponentType<?, ?> type : ComponentTypes.All) {
                this.addClassTag(type.dataClass(), new Tag(type.fileTag()));
            }
            for (Map.Entry<Class<?>, String> nestedData : ComponentTypes.NestedDataTags.entrySet()) {
                this.addClassTag(nestedData.getKey(), new Tag(nestedData.getValue()));
            }
            this.addClassTag(UUID.class, new Tag("!UUID"));
        }
    }

    private static class SceneDataConstructor extends Constructor {
        public SceneDataConstructor() {
            super(new LoaderOptions());

            this.addTypeDescription(new TypeDescription(SceneData.class, "!Scene"));
            for (ComponentType<?, ?> type : ComponentTypes.All) {
                this.addTypeDescription(new TypeDescription(type.dataClass(), type.fileTag()));
            }
            for (Map.Entry<Class<?>, String> nestedData : ComponentTypes.NestedDataTags.entrySet()) {
                this.addTypeDescription(new TypeDescription(nestedData.getKey(), nestedData.getValue()));
            }
            this.addTypeDescription(new TypeDescription(UUID.class, "!UUID"));
        }
    }
}
