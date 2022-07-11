package GameEngine.Engine.ECS;

import com.artemis.Component;

public class Entity {
    private final com.artemis.Entity entity;

    public Entity(com.artemis.Entity entity) {
        this.entity = entity;
    }

    public int getId() {
        return entity.getId();
    }

    public <T extends Component> Boolean hasComponent(Class<T> tClass) {
        return entity.getComponent(tClass) != null;
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return entity.getComponent(type);
    }

    public void addComponent(Component component) {
        entity.edit().add(component);
    }

    public <T extends Component> void removeComponent(Class<T> type) {
        entity.edit().remove(type);
    }
}
