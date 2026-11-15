package GameEngine.Engine.ECS;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import GameEngine.Engine.ECS.Components.IdComponent;
import com.artemis.Component;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ComponentTypesTest {

    @Test
    void everyComponentClassHasAComponentType() throws IOException, URISyntaxException {
        List<Class<?>> componentClasses = componentClasses();

        assertFalse(componentClasses.isEmpty());
        for (Class<?> componentClass : componentClasses) {
            boolean isRegistered =
                    ComponentTypes.All.stream().anyMatch(type -> type.componentClass() == componentClass);
            assertTrue(isRegistered, componentClass.getSimpleName() + " has no component type");
        }
    }

    @Test
    void everySavedClassHasItsOwnTag() {
        Set<String> tags = new HashSet<>(Set.of("!Scene", "!UUID"));
        for (ComponentType<?, ?> type : ComponentTypes.All) {
            assertTrue(tags.add(type.fileTag()), type.fileTag() + " is used twice");
        }
        for (String nestedTag : ComponentTypes.NestedDataTags.values()) {
            assertTrue(tags.add(nestedTag), nestedTag + " is used twice");
        }
    }

    @Test
    void everyComponentTypeSavesItsOwnDataClass() {
        Set<Class<?>> dataClasses = new HashSet<>();
        for (ComponentType<?, ?> type : ComponentTypes.All) {
            assertTrue(dataClasses.add(type.dataClass()), type.dataClass().getSimpleName() + " is used twice");
        }
    }

    private static List<Class<?>> componentClasses() throws IOException, URISyntaxException {
        Path componentsFolder = Path.of(IdComponent.class.getResource("").toURI());
        try (Stream<Path> files = Files.list(componentsFolder)) {
            return files.map(file -> file.getFileName().toString())
                    .filter(fileName -> fileName.endsWith(".class") && !fileName.contains("$"))
                    .map(fileName -> IdComponent.class.getPackageName() + "." + fileName.replace(".class", ""))
                    .<Class<?>>map(ComponentTypesTest::loadClass)
                    .filter(Component.class::isAssignableFrom)
                    .toList();
        }
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
