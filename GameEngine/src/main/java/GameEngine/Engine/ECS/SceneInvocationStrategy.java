package GameEngine.Engine.ECS;

import com.artemis.InvocationStrategy;

final class SceneInvocationStrategy extends InvocationStrategy {
    private boolean isProcessingSystems;
    private boolean isApplyingEntityChanges;

    void applyPendingEntityChanges() {
        if (isProcessingSystems) {
            return;
        }
        isApplyingEntityChanges = true;
        try {
            world.process();
        } finally {
            isApplyingEntityChanges = false;
        }
    }

    @Override
    protected void process() {
        if (isApplyingEntityChanges) {
            updateEntityStates();
            return;
        }
        isProcessingSystems = true;
        try {
            super.process();
        } finally {
            isProcessingSystems = false;
        }
    }
}
