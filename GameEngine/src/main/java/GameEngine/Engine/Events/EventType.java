package GameEngine.Engine.Events;

public enum EventType {
    None,

    // Window
    WindowClose,
    WindowResize,
    WindowFocus,
    WindowLostFocus,
    WindowMoved,

    // Application
    AppTick,
    AppUpdate,
    AppRender,

    // Keyboard
    KeyPressed,
    KeyReleased,
    KeyTyped,

    // Mouse
    MouseButtonPressed,
    MouseButtonReleased,
    MouseMoved,
    MouseScrolled
}
