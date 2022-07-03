package GameEngine.Engine.Events;

public abstract class KeyEvent extends Event {
    private final int keyCode;

    public KeyEvent(int keyCode) {
        super(EventCategory.Keyboard);
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }

    @Override
    public String toString() {
        return super.toString() + ", keyCode=" + keyCode;
    }

    public static class KeyPressedEvent extends KeyEvent {
        public int repeatCount;

        public KeyPressedEvent(int keyCode, int repeatCount) {
            super(keyCode);
            this.repeatCount = repeatCount;
        }

        @Override
        public EventType getEventType() {
            return EventType.KeyPressed;
        }

        @Override
        public String toString() {
            return super.toString() + ", repeatCount=" + repeatCount;
        }
    }

    public static class KeyReleasedEvent extends KeyEvent {

        public KeyReleasedEvent(int keyCode) {
            super(keyCode);
        }

        @Override
        public EventType getEventType() {
            return EventType.KeyReleased;
        }
    }

    public static class KeyTypedEvent extends KeyEvent {

        public KeyTypedEvent(int keyCode) {
            super(keyCode);
        }

        @Override
        public EventType getEventType() {
            return EventType.KeyTyped;
        }
    }
}
