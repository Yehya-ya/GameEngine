package GameEngine.Engine.Events;

import org.jetbrains.annotations.NotNull;

public abstract class Event {
    public boolean handled;
    public EventCategory category;

    public Event(EventCategory category) {
        this.category = category;
    }

    public abstract EventType getEventType();


    public boolean isInCategory(@NotNull EventCategory category) {
        return (this.category.value & category.value) != 0;
    }

    @Override
    public String toString() {
        return getEventType().name() + ": handled=" + handled;
    }
}

