package Engine.Renderer.Buffer;

import java.util.Vector;

import static Engine.Utils.YH_Log.YH_ASSERT;

public class BufferLayout {
    private final Vector<BufferElement> bufferElements;
    private int offset;

    public BufferLayout() {
        bufferElements = new Vector<>();
        offset = 0;
    }

    public BufferLayout addBufferElement(ShaderDataType type, String name, boolean normalized) {
        bufferElements.add(new BufferElement(type, name, normalized, offset));
        offset += type.getSize();
        return this;
    }

    public Vector<BufferElement> getBufferElements() {
        return bufferElements;
    }

    public int getOffset() {
        return offset;
    }

    public enum ShaderDataType {
        None, Float, Float2, Float3, Float4, Mat3, Mat4, Int, Int2, Int3, Int4, Bool;

        public int getSize() {
            switch (this) {
                case Float:
                    return java.lang.Float.BYTES;
                case Float2:
                    return java.lang.Float.BYTES * 2;
                case Float3:
                    return java.lang.Float.BYTES * 3;
                case Float4:
                    return java.lang.Float.BYTES * 4;
                case Mat3:
                    return java.lang.Float.BYTES * 3 * 3;
                case Mat4:
                    return java.lang.Float.BYTES * 4 * 4;
                case Int:
                    return Integer.BYTES;
                case Int2:
                    return Integer.BYTES * 2;
                case Int3:
                    return Integer.BYTES * 3;
                case Int4:
                    return Integer.BYTES * 4;
                case Bool:
                    return 1;
            }

            YH_ASSERT(false, "Unknown ShaderDataType!");
            return 0;
        }
    }

    public static class BufferElement {
        public final String name;
        public final ShaderDataType type;
        public final int size;
        public final int offset;
        public final boolean normalized;

        public BufferElement(ShaderDataType type, String name, boolean normalized, int offset) {
            this.name = name;
            this.type = type;
            this.offset = offset;
            this.size = type.getSize();
            this.normalized = normalized;
        }

        public int GetComponentCount() {
            switch (type) {
                case Float:
                    return 1;
                case Float2:
                    return 2;
                case Float3:
                    return 3;
                case Float4:
                    return 4;
                case Mat3:
                    return 3 * 3;
                case Mat4:
                    return 4 * 4;
                case Int:
                    return 1;
                case Int2:
                    return 2;
                case Int3:
                    return 3;
                case Int4:
                    return 4;
                case Bool:
                    return 1;
            }

            YH_ASSERT(false, "Unknown ShaderDataType!");
            return 0;
        }
    }
}
