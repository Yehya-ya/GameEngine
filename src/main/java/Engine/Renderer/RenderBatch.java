package Engine.Renderer;

import Engine.Listeners.KeyListener;

import static org.lwjgl.glfw.GLFW.*;

public class RenderBatch {

    Object object;

    public RenderBatch() {
        this.object = new AObject();
    }

    public void init() {
        this.object.init();
    }

    public void render() {
        if (KeyListener.isKeyPressed(GLFW_KEY_1)) {
            this.object.destroy();
            this.object = new AObject();
            this.object.init();
        } else if (KeyListener.isKeyPressed(GLFW_KEY_2)) {
            this.object.destroy();
            this.object = new EObject();
            this.object.init();
        } else if (KeyListener.isKeyPressed(GLFW_KEY_3)) {
            this.object.destroy();
            this.object = new NObject();
            this.object.init();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_T)) {
            this.object.setMode(Object.Mode.TRIANGLES);
        } else if (KeyListener.isKeyPressed(GLFW_KEY_L)) {
            this.object.setMode(Object.Mode.LINES);
        } else if (KeyListener.isKeyPressed(GLFW_KEY_P)) {
            this.object.setMode(Object.Mode.POINTS);
        }

        this.object.render();
    }

    public void destroy() {
        this.object.destroy();
    }
}
