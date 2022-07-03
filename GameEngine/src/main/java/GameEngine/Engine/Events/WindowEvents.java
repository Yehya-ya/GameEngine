package GameEngine.Engine.Events;

public class WindowEvents {
    public static class WindowCloseEvent extends Event {
        public WindowCloseEvent() {
            super(EventCategory.Application);
        }

        @Override
        public EventType getEventType() {
            return EventType.WindowClose;
        }
    }

    public static class WindowResizeEvent extends Event {
        private final int width, height;

        public WindowResizeEvent(int width, int height) {
            super(EventCategory.Application);
            this.width = width;
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        @Override
        public EventType getEventType() {
            return EventType.WindowResize;
        }

        @Override
        public String toString() {
            return super.toString() + ", width=" + width + ", height=" + height;
        }
    }

    public static class WindowFocusEvent extends Event {
        public WindowFocusEvent() {
            super(EventCategory.Application);
        }

        @Override
        public EventType getEventType() {
            return EventType.WindowFocus;
        }
    }

    public static class WindowLostFocusEvent extends Event {
        public WindowLostFocusEvent() {
            super(EventCategory.Application);
        }

        @Override
        public EventType getEventType() {
            return EventType.WindowLostFocus;
        }
    }

    public static class WindowMovedEvent extends Event {
        public WindowMovedEvent() {
            super(EventCategory.Application);
        }

        @Override
        public EventType getEventType() {
            return EventType.WindowMoved;
        }
    }
}
