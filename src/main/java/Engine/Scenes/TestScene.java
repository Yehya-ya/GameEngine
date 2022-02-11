package Engine.Scenes;

import Engine.Listeners.KeyListener;
import Engine.Renderer.ColorCircle;
import Engine.Window;

import static org.lwjgl.glfw.GLFW.*;

public class TestScene extends Scene {
    public static final int ID = 101;

    private ColorCircle object;

    public TestScene() {
        super();
        this.object = new ColorCircle(this);
    }

    @Override
    public void init() {
        this.object.init();
    }

    @Override
    public void update(float dt) {
        this.object.update(dt);

        if (KeyListener.isKeyPressed(GLFW_KEY_F1)) {
            boolean isFullScreen = Window.isFullScreen();
            Window.setFullScreen(!isFullScreen);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.close();
        }

        this.object.render();
    }

    @Override
    public void updateAspectRatio() {
        super.updateAspectRatio();
        this.object.updateAR();
    }
}
