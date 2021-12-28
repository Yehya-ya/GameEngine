package Engine.Renderer;

import static org.lwjgl.opengl.GL45.*;

public abstract class Object {
    static final int POS_SIZE = 3;
    static final int COLOR_SIZE = 4;
    static final int POS_OFFSET = 0;
    static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    static final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE;
    static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    protected Mode mode;
    protected int vaoID, vboID, eboID;

    float[] vertices;
    int[] indices;

    public Object(Mode mode) {
        this.mode = mode;
    }

    abstract void init();

    abstract void render();

    abstract void destroy();

    public int getMode() {
        return mode.value;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public enum Mode {
        TRIANGLES(GL_TRIANGLES), LINES(GL_LINES), POINTS(GL_POINTS);
        private final int value;

        Mode(int value) {
            this.value = value;
        }
    }
}
