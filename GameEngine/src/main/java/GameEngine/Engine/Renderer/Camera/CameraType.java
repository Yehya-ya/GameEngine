package GameEngine.Engine.Renderer.Camera;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static GameEngine.Engine.Utils.YH_Log.YH_ASSERT;

public enum CameraType {
    Orthographic, Perspective;

    @Contract(pure = true)
    public static @NotNull CameraType getInstanceFromIndex(int index) {
        switch (index) {
            case 0 -> {
                return Orthographic;
            }
            case 1 -> {
                return Perspective;
            }
        }

        YH_ASSERT(false, "unknown index " + index);
        return null;
    }
}
