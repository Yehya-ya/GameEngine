package GameEngine.Engine.ECS;

import GameEngine.Engine.Core.Project;
import com.artemis.Component;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public record ComponentType<C extends Component, D>(
        String fileTag,
        Class<C> componentClass,
        Class<D> dataClass,
        UnaryOperator<C> copier,
        BiFunction<C, Project, D> saver,
        BiFunction<D, Project, C> loader) {

    public Component copy(Component component) {
        return copier.apply(componentClass.cast(component));
    }

    public Object save(Component component, Project project) {
        return saver.apply(componentClass.cast(component), project);
    }

    public Component load(Object data, Project project) {
        return loader.apply(dataClass.cast(data), project);
    }
}
