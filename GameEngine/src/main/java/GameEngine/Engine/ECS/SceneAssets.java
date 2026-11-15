package GameEngine.Engine.ECS;

import GameEngine.Engine.Core.Project;
import GameEngine.Engine.Renderer.Texture;
import GameEngine.Engine.Renderer.TextureLibrary;
import org.jetbrains.annotations.NotNull;

public record SceneAssets(@NotNull Project project, @NotNull TextureLibrary textures) {
    public @NotNull Texture texture(@NotNull String projectPath) {
        return textures.get(project.resolve(projectPath));
    }
}
