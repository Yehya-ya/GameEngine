package Engine.Utils;


public class TimeStep {
    private final float time;

    public TimeStep(float time) {
        this.time = time;
    }

    public float getMilliseconds() {
        return time;
    }

    public float getSeconds() {
        return time * 1000.0f;
    }
}
