package Engine;

import Engine.Events.Event;
import Engine.Utils.TimeStep;

public abstract class Layer {

    protected String name;

    public Layer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void onUpdate(TimeStep timeStep) {
    }

    public void onAttach() {
    }

    public void onDetach() {
    }

    public void onEvent(Event event) {
    }

    public void onImgRender() {
    }
}
