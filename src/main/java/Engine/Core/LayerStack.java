package Engine.Core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

import static Engine.Utils.YH_Log.YH_LOG_TRACE;

public class LayerStack implements Iterable<Layer> {

    private final ArrayList<Layer> layers;
    private int insertIndex;

    public LayerStack() {
        YH_LOG_TRACE("Creating the layers stack.");
        layers = new ArrayList<>();
        insertIndex = 0;
    }

    public void delete() {
        for (Layer layer : layers) {
            layer.onDetach();
        }
        YH_LOG_TRACE("Deleting the layers stack.");
    }

    public int size() {
        return layers.size();
    }

    public Layer get(int i) {
        return layers.get(i);
    }

    public void pushLayer(@NotNull Layer layer) {
        layer.onAttach();
        layers.add(insertIndex, layer);
        insertIndex++;
    }

    public void pushOverlay(@NotNull Layer layer) {
        layer.onAttach();
        layers.add(layer);
    }

    public void popLayer(Layer layer) {
        int index = layers.indexOf(layer);
        if (index != -1) {
            layer.onDetach();
            layers.remove(layer);
            insertIndex--;
        }
    }

    public void popOverlay(Layer layer) {
        int index = layers.indexOf(layer);
        if (index != -1) {
            layer.onDetach();
            layers.remove(layer);
        }
    }

    @NotNull
    @Override
    public Iterator<Layer> iterator() {
        return layers.iterator();
    }
}
