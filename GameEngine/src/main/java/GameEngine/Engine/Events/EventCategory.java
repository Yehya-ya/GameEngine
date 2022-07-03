package GameEngine.Engine.Events;

public enum EventCategory {
    None(0), Application(1 << 1), Input(1 << 2), Keyboard(1 << 3), Mouse(1 << 4);

    public final int value;
    EventCategory(int value) {
        this.value = value;
    }
}
