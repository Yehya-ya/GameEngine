package GameEngine.Engine.Events;

public enum EventType {
    None, //
    WindowClose, WindowResize, WindowFocus, WindowLostFocus, WindowMoved, //
    AppTick, AppUpdate, AppRender, //
    KeyPressed, KeyReleased, KeyTyped, //
    MouseButtonPressed, MouseButtonReleased, MouseMoved, MouseScrolled //
}
