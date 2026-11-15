package Panels;

import GameEngine.Engine.ECS.Entity;
import com.artemis.Component;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public record ComponentEditor<C extends Component>(
        String menuName, String title, Class<C> componentClass, Supplier<C> creator, BiConsumer<Entity, C> drawer) {

    public boolean isOn(Entity entity) {
        return entity.getComponent(componentClass) != null;
    }

    public void addTo(Entity entity) {
        entity.addComponent(creator.get());
    }

    public void draw(Entity entity) {
        drawer.accept(entity, entity.getComponent(componentClass));
    }
}
