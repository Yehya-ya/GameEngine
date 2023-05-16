package GameEngine.Engine.ECS.Components;

import com.artemis.Component;

import java.util.UUID;

public class IdComponent extends Component {
    public final UUID id;

    public IdComponent() {
        this(UUID.randomUUID());
    }

    public IdComponent(UUID uuid) {
        id = uuid;
    }
}
