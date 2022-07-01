package Engine.Renderer;

public class RendererStatistics {
    private static final RendererStatistics instance = new RendererStatistics();
    private int drawCallsCount = 0;
    private int QuadsCount = 0;

    private RendererStatistics() {
    }

    public static RendererStatistics getInstance() {
        return instance;
    }

    public int getDrawCallsCount() {
        return drawCallsCount;
    }

    public int getQuadsCount() {
        return QuadsCount;
    }

    public void addOneToQuadsCount() {
        QuadsCount++;
    }

    public void addOneToDrawCallsCount() {
        drawCallsCount++;
    }

    public void reset() {
        drawCallsCount = 0;
        QuadsCount = 0;
    }
}
