package Engine.Events;

import Engine.YH_Log;

public class EventDispatcher {
    private Event event;

    public EventDispatcher(Event event) {
        this.event = event;
    }

    public boolean dispatch(EventType eventType, Handler handler){
        if (event.getEventType().equals(eventType)) {
            event.handled = handler.handle(event);
            return true;
        }
        return false;
    }

    public interface Handler{
         boolean handle(Event event);
    }
}
