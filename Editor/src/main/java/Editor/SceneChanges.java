package Editor;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.ECS.SceneSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SceneChanges {
    private final Project project;
    private @Nullable String savedSceneYaml;

    SceneChanges(@NotNull Project project) {
        this.project = project;
    }

    void markSaved(@NotNull Scene scene) {
        savedSceneYaml = SceneSerializer.toYaml(scene, project);
    }

    boolean hasUnsavedChanges(@Nullable Scene scene) {
        return scene != null && !SceneSerializer.toYaml(scene, project).equals(savedSceneYaml);
    }
}
