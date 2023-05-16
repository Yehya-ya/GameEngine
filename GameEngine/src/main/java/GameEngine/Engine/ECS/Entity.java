package GameEngine.Engine.ECS;

import GameEngine.Engine.ECS.Components.IdComponent;
import com.artemis.Component;
import com.artemis.utils.Bag;

import java.util.UUID;

public class Entity {
    private final com.artemis.Entity entity;

    public Entity(com.artemis.Entity entity) {
        this.entity = entity;
    }

    public int getId() {
        return entity.getId();
    }

    public UUID GetUUID() {
        return getComponent(IdComponent.class).id;
    }

    public <T extends Component> Boolean hasComponent(Class<T> tClass) {
        return entity.getComponent(tClass) != null;
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return entity.getComponent(type);
    }

    public Bag<Component> getAllComponents() {
        Bag<Component> bag = new Bag<>();
        return entity.getComponents(bag);
    }

    public void addComponent(Component component) {
        entity.edit().add(component);
    }

    public <T extends Component> void removeComponent(Class<T> type) {
        entity.edit().remove(type);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Entity) {
            return ((Entity) obj).getId() == this.getId();
        }
        return false;
    }
}
