package GameEngine.Engine.Core;

import GameEngine.Engine.Events.Event;
import GameEngine.Engine.Utils.TimeStep;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_TRACE;

public abstract class Layer {

    protected String name;

    public Layer(String name) {
        YH_LOG_TRACE("Creating the \"{}\" layer.", name);
        this.name = name;
    }

    public void delete() {
        YH_LOG_TRACE("Deleting the \"{}\" layer.", name);
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
