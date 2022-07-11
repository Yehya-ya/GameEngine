package GameEngine.Engine.ECS.Components;

import com.artemis.Component;

public class TagComponent extends Component {
    public String name;

    public TagComponent() {
        this("Untitled");
    }

    public TagComponent(String name) {
        this.name = name;
    }
}
