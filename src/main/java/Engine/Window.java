package Engine;

import Engine.Events.Event;

import static Engine.Utils.YH_Log.YH_LOG_INFO;

public abstract class Window {
    protected final WindowProp prop;

    public Window(WindowProp prop) {
        this.prop = prop;
        YH_LOG_INFO("Creating a window \"{}\" ({}, {})", prop.title, prop.width, prop.height);
        init();
    }

    public void destroy() {
        YH_LOG_INFO("Destroying the window \"{}\"", prop.title);
        shutdown();
    }

    public abstract void onUpdate();

    protected abstract void init();

    protected abstract void shutdown();

    public int getHeight() {
        return prop.height;
    }

    public int getWidth() {
        return prop.width;
    }

    public boolean isFullScreen() {
        return prop.isFullScreen;
    }

    public void toggleFullScreen() {
        prop.isFullScreen = !prop.isFullScreen;
    }

    public boolean getVSync() {
        return prop.vSync;
    }

    public void setVSync(boolean vSync) {
        prop.vSync = vSync;
    }

    public void setEventCallback(EventCallBackHandler callback) {
        this.prop.setEventCallback(callback);
    }

    public interface EventCallBackHandler {
        void invoke(Event event);
    }

    public static class WindowProp {
        public String title;
        public int width, height;
        public boolean isFullScreen;
        public boolean vSync;
        public EventCallBackHandler eventCallBack;

        public WindowProp(String title, int width, int height, boolean isFullScreen, boolean vSync) {
            this.title = title;
            this.width = width;
            this.height = height;
            this.isFullScreen = isFullScreen;
            this.vSync = vSync;
        }

        public WindowProp(String title, int width, int height, boolean isFullScreen) {
            this(title, width, height, isFullScreen, true);
        }

        public WindowProp(String title, int width, int height) {
            this(title, width, height, false, true);
        }

        public WindowProp(String title) {
            this(title, 1280, 720, false, true);
        }

        public WindowProp() {
            this("default window", 1280, 720, false, true);
        }

        public void setEventCallback(EventCallBackHandler callback) {
            this.eventCallBack = callback;
        }
    }
}
