package Engine;

import Engine.Events.Event;
import org.lwjgl.system.Callback;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.libffi.FFICIF;

public abstract class Window {
    protected final WindowProp prop;

    public Window(WindowProp prop) {
        this.prop = prop;
        YH_Log.info("Creating window {0} ({1}, {2})", prop.title, prop.width, prop.height);
        init();
    }

    public abstract void onUpdate();
    public abstract void init();
    public abstract void shutdown();

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

    public interface EventCallBackHandler {
        void invoke(Event event);
    }
}
