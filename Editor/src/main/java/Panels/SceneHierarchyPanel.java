package Panels;

import GameEngine.Engine.ECS.Components.CameraComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.Renderer.Camera.CameraType;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;
import GameEngine.Engine.Renderer.Camera.PerspectiveCamera;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import org.jetbrains.annotations.NotNull;

public class SceneHierarchyPanel {
    private Scene scene;
    private Entity selectedEntity;


    public SceneHierarchyPanel(Scene scene) {
        setScene(scene);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void onImGuiRender() {
        ImGui.begin("Scene Hierarchy");

        TagManager tagManager = scene.getTagManager();
        for (String tag : tagManager.getRegisteredTags()) {
            drawEntityNode(tagManager.getEntity(tag), tag);
        }

        ImGui.end();

        ImGui.begin("Properties");

        if (selectedEntity != null) {
            drawEntityProperties(selectedEntity);
        }

        ImGui.end();
    }

    private void drawEntityNode(@NotNull Entity e, String tag) {
        int flags = ImGuiTreeNodeFlags.OpenOnArrow | (e == selectedEntity ? ImGuiTreeNodeFlags.Selected : 0);
        boolean opened = ImGui.treeNodeEx(e.getId(), flags, tag);
        if (ImGui.isItemClicked()) {
            selectedEntity = e;
        }
        if (opened) {
            ImGui.treePop();
        }
    }

    private void drawEntityProperties(Entity e) {
        Bag<Component> componentBag = new Bag<>();
        e.getComponents(componentBag);

        int flags = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen;

        for (Component component : componentBag) {
            if (component instanceof TransformComponent transformComponent) {
                if (ImGui.treeNodeEx("Transform" + e.getId(), flags, "Transform")) {
                    float[] size = new float[]{transformComponent.size.x, transformComponent.size.y};
                    if (ImGui.dragFloat2("Size", size, 0.01f)) {
                        transformComponent.size.x = size[0];
                        transformComponent.size.y = size[1];
                    }

                    float[] transform = new float[]{transformComponent.transform.x, transformComponent.transform.y, transformComponent.transform.z};
                    if (ImGui.dragFloat3("Transform", transform, 0.01f)) {
                        transformComponent.transform.x = transform[0];
                        transformComponent.transform.y = transform[1];
                        transformComponent.transform.z = transform[2];
                    }

                    float[] rotationAngle = new float[]{transformComponent.rotationAngle};
                    if (ImGui.dragFloat("Rotation Angle", rotationAngle, 0.5f)) {
                        transformComponent.rotationAngle = rotationAngle[0];
                    }
                    ImGui.treePop();
                }
            }

            if (component instanceof CameraComponent cameraComponent) {
                if (ImGui.treeNodeEx("Camera" + e.getId(), flags, "Camera")) {
                    ImBoolean imBoolean = new ImBoolean(cameraComponent.primary);
                    ImGui.checkbox("Primary Camera", imBoolean);
                    cameraComponent.primary = imBoolean.get();

                    float[] far = new float[]{cameraComponent.camera.getFar()};
                    if (ImGui.dragFloat("Far", far, 0.01f)) {
                        cameraComponent.camera.setFar(far[0]);
                    }

                    float[] near = new float[]{cameraComponent.camera.getNear()};
                    if (ImGui.dragFloat("Near", near, 0.01f)) {
                        cameraComponent.camera.setNear(near[0]);
                    }

                    ImInt imInt = new ImInt(cameraComponent.cameraType.ordinal());
                    ImGui.listBox("Type", imInt, new String[]{CameraType.Orthographic.name(), CameraType.Perspective.name()});
                    if (cameraComponent.cameraType != CameraType.getInstanceFromIndex(imInt.get())) {
                        cameraComponent.cameraType = CameraType.getInstanceFromIndex(imInt.get());
                        if (cameraComponent.cameraType == CameraType.Perspective) {
                            cameraComponent.camera = new PerspectiveCamera(cameraComponent.camera.getAspectRatio());
                        } else if (cameraComponent.cameraType == CameraType.Orthographic) {
                            cameraComponent.camera = new OrthographicCamera(cameraComponent.camera.getAspectRatio());
                        }
                    }

                    if (cameraComponent.camera instanceof OrthographicCamera orthographicCamera) {
                        float[] zoom = new float[]{orthographicCamera.getZoomLevel()};
                        if (ImGui.dragFloat("Zoom", zoom, 0.01f)) {
                            orthographicCamera.setZoomLevel(zoom[0]);
                        }
                    } else if (cameraComponent.camera instanceof PerspectiveCamera perspectiveCamera) {
                        float[] fov = new float[]{perspectiveCamera.getFov()};
                        if (ImGui.dragFloat("Fov", fov, 0.1f)) {
                            perspectiveCamera.setFov(fov[0]);
                        }
                    }


                    ImGui.treePop();
                }
            }
        }

    }
}
