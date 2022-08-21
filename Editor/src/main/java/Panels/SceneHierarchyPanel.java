package Panels;

import GameEngine.Engine.ECS.Components.CameraComponent;
import GameEngine.Engine.ECS.Components.SpriteComponent;
import GameEngine.Engine.ECS.Components.TagComponent;
import GameEngine.Engine.ECS.Components.TransformComponent;
import GameEngine.Engine.ECS.Entity;
import GameEngine.Engine.ECS.Scene;
import GameEngine.Engine.Renderer.Camera.CameraType;
import GameEngine.Engine.Renderer.Camera.OrthographicCamera;
import GameEngine.Engine.Renderer.Camera.PerspectiveCamera;
import GameEngine.Engine.Renderer.Texture;
import Utils.FileDialog;
import com.artemis.Component;
import com.artemis.utils.IntBag;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Function;

public class SceneHierarchyPanel {
    public static final long OpenTextureFileDialogId = 201;
    private Scene scene;
    private Entity selectedEntity;

    public SceneHierarchyPanel() {

    }

    public SceneHierarchyPanel(Scene scene) {
        setScene(scene);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        this.selectedEntity = null;
    }

    public void onImGuiRender() {
        ImGui.begin("Scene Hierarchy");

        if (scene != null) {
            IntBag entitiesIds = scene.getEntitiesIds(TagComponent.class);
            for (int i = 0; i < entitiesIds.size(); i++) {
                drawEntityNode(scene.getEntity(entitiesIds.get(i)));
            }

            if (ImGui.isMouseDown(0) && ImGui.isWindowHovered()) {
                selectedEntity = null;
            }

            if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonRight)) {
                if (ImGui.menuItem("Create Empty Entity")) {
                    scene.createEntity();
                }

                ImGui.endPopup();
            }
        }

        ImGui.end();

        ImGui.begin("Properties");

        if (selectedEntity != null) {
            drawEntityProperties(selectedEntity);

            if (ImGui.button("Add Component")) {
                ImGui.openPopup("AddComponent");
            }

            if (ImGui.beginPopup("AddComponent")) {
                if (ImGui.menuItem("Transformation Component")) {
                    selectedEntity.addComponent(new TransformComponent());
                    ImGui.closeCurrentPopup();
                }

                if (ImGui.menuItem("Camera Component")) {
                    selectedEntity.addComponent(new CameraComponent());
                    ImGui.closeCurrentPopup();
                }

                if (ImGui.menuItem("Sprite Renderer Component")) {
                    selectedEntity.addComponent(new SpriteComponent());
                    ImGui.closeCurrentPopup();
                }

                ImGui.endPopup();
            }
        }

        ImGui.end();
    }

    private void drawEntityNode(@NotNull Entity e) {
        String tag = e.getComponent(TagComponent.class).name;
        int flags = ImGuiTreeNodeFlags.OpenOnArrow | (e == selectedEntity ? ImGuiTreeNodeFlags.Selected : 0);
        boolean opened = ImGui.treeNodeEx(e.getId(), flags, tag);
        if (ImGui.isItemClicked()) {
            selectedEntity = e;
        }
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("Delete Entity")) {
                scene.deleteEntity(e);
            }

            ImGui.endPopup();
        }
        if (opened) {
            ImGui.treePop();
        }
    }

    private void drawEntityProperties(@NotNull Entity e) {
        TagComponent tagComponent = e.getComponent(TagComponent.class);
        if (tagComponent != null) {
            ImString imString = new ImString(tagComponent.name, 100);
            int flag = ImGuiInputTextFlags.EnterReturnsTrue;
            if (ImGui.inputText("##" + tagComponent.name, imString, flag)) {
                tagComponent.name = imString.get();
            }
        }

        drawEntityComponentProperties("Transform", e, TransformComponent.class, transformComponent -> {
            drawVector3Editor("Size", transformComponent.size, 1.0f);
            drawVector3Editor("Transform", transformComponent.translate);
            drawVector3Editor("Rotation", transformComponent.rotation);

            return null;
        });

        drawEntityComponentProperties("Camera", e, CameraComponent.class, cameraComponent -> {
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

            return null;
        });

        drawEntityComponentProperties("Sprite", e, SpriteComponent.class, spriteComponent -> {
            int type = spriteComponent.texture == null ? 0 : 1;
            ImInt imInt = new ImInt(type);
            ImGui.listBox("Type", imInt, new String[]{"color", "texture"});
            if (type != imInt.get()) {
                if (imInt.get() == 0) {
                    spriteComponent.setToColor();
                } else if (imInt.get() == 1) {
                    spriteComponent.setToTexture();
                }
            }

            if (spriteComponent.texture == null) {
                float[] color = new float[]{spriteComponent.color.x, spriteComponent.color.y, spriteComponent.color.z, spriteComponent.color.w};
                if (ImGui.colorEdit4("Color" + e.getId(), color)) {
                    spriteComponent.color.x = color[0];
                    spriteComponent.color.y = color[1];
                    spriteComponent.color.z = color[2];
                    spriteComponent.color.w = color[3];
                }
            } else {
                float[] tilingFactor = new float[]{spriteComponent.tilingFactor};
                if (ImGui.dragFloat("Tiling Factor", tilingFactor, 0.1f)) {
                    spriteComponent.tilingFactor = tilingFactor[0];
                }

                if (ImGui.imageButton(spriteComponent.texture.getRendererId(), 50, 50)) {
                    FileDialog.openFile(OpenTextureFileDialogId, "JPEG (*.JPG; *.JPEG; *.JPE){.jpg,.jpeg,jpe}PNG (.PNG){.png}", () -> spriteComponent.texture = Texture.create(ImGuiFileDialog.getFilePathName()));
                }
            }
            return null;
        });
    }

    private <T extends Component> void drawEntityComponentProperties(String label, @NotNull Entity entity, Class<T> componentType, Function<T, Void> function) {
        int flags = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen;

        T component = entity.getComponent(componentType);
        if (component != null) {
            if (ImGui.treeNodeEx(label + entity.getId(), flags, label)) {
                function.apply(component);
                ImGui.treePop();
            }
        }
    }

    private void drawVector3Editor(String label, Vector3f vector) {
        drawVector3Editor(label, vector, 0f);
    }

    private void drawVector3Editor(String label, @NotNull Vector3f vector, float resetValue) {
        float columnWidth = 100f;
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float multiItemsWidth = ImGui.calcItemWidth() / 3;
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePadding().y * 2.0f;
        ImVec2 buttonSize = new ImVec2(lineHeight + 3.0f, lineHeight);

        float[] valueX = new float[]{vector.x};
        if (drawVectorXEditor(valueX, resetValue, multiItemsWidth, buttonSize.x, buttonSize.y)) {
            vector.x = valueX[0];
        }

        float[] valueY = new float[]{vector.y};
        if (drawVectorYEditor(valueY, resetValue, multiItemsWidth, buttonSize.x, buttonSize.y)) {
            vector.y = valueY[0];
        }

        float[] valueZ = new float[]{vector.z};
        if (drawVectorZEditor(valueZ, resetValue, multiItemsWidth, buttonSize.x, buttonSize.y)) {
            vector.z = valueZ[0];
        }

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }

    private boolean drawVectorXEditor(float[] value, float resetValue, float width, float buttonWidth, float buttonHeight) {
        boolean isChanged = false;
        ImGui.pushItemWidth(width);

        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        if (ImGui.button("X", buttonWidth, buttonHeight)) {
            value[0] = resetValue;
            isChanged = true;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] valueX = new float[]{value[0]};
        if (ImGui.dragFloat("##X", valueX, 0.1f, 0.0f, 0.0f, "%.2f")) {
            value[0] = valueX[0];
            isChanged = true;
        }
        ImGui.popItemWidth();
        return isChanged;
    }

    private boolean drawVectorYEditor(float[] value, float resetValue, float width, float buttonWidth, float buttonHeight) {
        boolean isChanged = false;
        ImGui.pushItemWidth(width);
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        if (ImGui.button("Y", buttonWidth, buttonHeight)) {
            value[0] = resetValue;
            isChanged = true;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] valueY = new float[]{value[0]};
        if (ImGui.dragFloat("##Y", valueY, 0.1f, 0.0f, 0.0f, "%.2f")) {
            value[0] = valueY[0];
            isChanged = true;
        }
        ImGui.popItemWidth();
        return isChanged;
    }

    private boolean drawVectorZEditor(float[] value, float resetValue, float width, float buttonWidth, float buttonHeight) {
        boolean isChanged = false;
        ImGui.pushItemWidth(width);
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.25f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.35f, 0.9f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.25f, 0.8f, 1.0f);
        if (ImGui.button("Z", buttonWidth, buttonHeight)) {
            value[0] = resetValue;
            isChanged = true;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] valueZ = new float[]{value[0]};
        if (ImGui.dragFloat("##Z", valueZ, 0.1f, 0.0f, 0.0f, "%.2f")) {
            value[0] = valueZ[0];
            isChanged = true;
        }
        ImGui.popItemWidth();
        return isChanged;
    }
}
