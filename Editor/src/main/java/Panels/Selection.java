package Panels;

import GameEngine.Engine.ECS.Entity;

public class Selection {
    private Entity entity;

    public Entity getEntity() {
        return entity;
    }

    public boolean isSelected(Entity candidate) {
        return entity != null && entity.equals(candidate);
    }

    public void select(Entity entity) {
        this.entity = entity;
    }

    public void clear() {
        entity = null;
    }
}
