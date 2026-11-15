package GameEngine.Engine.Core;

import org.jetbrains.annotations.NotNull;

public enum BuildType {
    Debug,
    Release;

    public static final String systemProperty = "gameengine.build";

    public static @NotNull BuildType current() {
        boolean isReleaseBuild = Release.name().equalsIgnoreCase(System.getProperty(systemProperty));
        return isReleaseBuild ? Release : Debug;
    }

    public boolean isDebug() {
        return this == Debug;
    }
}
