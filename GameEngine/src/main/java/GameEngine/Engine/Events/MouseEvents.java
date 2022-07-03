package GameEngine.Engine.Events;

public class MouseEvents {
    public static class MouseButtonPressedEvent extends Event {
        private final int button;

        public MouseButtonPressedEvent(int button) {
            super(EventCategory.Mouse);
            this.button = button;
        }

        @Override
        public EventType getEventType() {
            return EventType.MouseButtonPressed;
        }

        @Override
        public String toString() {
            return super.toString() + ", button=" + button;
        }
    }

    public static class MouseButtonReleasedEvent extends Event {
        private final int button;

        public MouseButtonReleasedEvent(int button) {
            super(EventCategory.Mouse);
            this.button = button;
        }

        @Override
        public EventType getEventType() {
            return EventType.MouseButtonReleased;
        }

        @Override
        public String toString() {
            return super.toString() + ", button=" + button;
        }
    }

    public static class MouseMovedEvent extends Event {
        private final double mouseX, mouseY;

        public MouseMovedEvent(double x, double y) {
            super(EventCategory.Mouse);
            this.mouseX = x;
            this.mouseY = y;
        }

        public double getMouseX() {
            return mouseX;
        }

        public double getMouseY() {
            return mouseY;
        }

        @Override
        public EventType getEventType() {
            return EventType.MouseMoved;
        }

        @Override
        public String toString() {
            return super.toString() + ", mouseX=" + mouseX + ", mouseY=" + mouseY;
        }
    }

    public static class MouseScrolledEvent extends Event {
        private final double xOffset, yOffset;

        public MouseScrolledEvent(double xOffset, double yOffset) {
            super(EventCategory.Mouse);
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        public double getxOffset() {
            return xOffset;
        }

        public double getyOffset() {
            return yOffset;
        }

        @Override
        public EventType getEventType() {
            return EventType.MouseScrolled;
        }

        @Override
        public String toString() {
            return super.toString() + ", xOffset=" + xOffset + ", yOffset=" + yOffset;
        }
    }
}
