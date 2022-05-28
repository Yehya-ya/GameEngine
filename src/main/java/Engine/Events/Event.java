package Engine.Events;

public abstract class Event {
    public boolean handled;
    public abstract EventType  getEventType();

    @Override
    public String toString() {
        return  getEventType().name() + ": handled=" + handled;
    }
}

