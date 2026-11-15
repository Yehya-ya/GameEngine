package Panels;

import static GameEngine.Engine.Utils.YH_Log.YH_LOG_ERROR;

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
import GameEngine.Engine.Renderer.TextureLibrary;
import GameEngine.Engine.Renderer.TextureLoadException;
import Utils.FileDialog;
import com.artemis.utils.IntBag;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SceneHierarchyPanel {
    public static final String HierarchyWindowName = "Scene Hierarchy";
    public static final String PropertiesWindowName = "Properties";
    private static final String imageFileTypes = "Images{" + String.join(",", Texture.ImageExtensions) + "}";
    private final Selection selection;
    private final List<ComponentEditor<?>> componentEditors = List.of(
            new ComponentEditor<>(
                    "Transformation Component",
                    "Transform",
                    TransformComponent.class,
                    TransformComponent::new,
                    this::drawTransform),
            new ComponentEditor<>(
                    "Camera Component",
                    "Camera",
                    CameraComponent.class,
                    () -> new CameraComponent(new OrthographicCamera(1.0f)),
                    this::drawCamera),
            new ComponentEditor<>(
                    "Sprite Renderer Component",
                    "Sprite",
                    SpriteComponent.class,
                    SpriteComponent::new,
                    this::drawSprite));
    private final TextureLibrary textures;
    private final FileDialog fileDialog;
    private Scene scene;

    public SceneHierarchyPanel(Selection selection, TextureLibrary textures, FileDialog fileDialog) {
        this.selection = selection;
        this.textures = textures;
        this.fileDialog = fileDialog;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void onImGuiRender() {
        ImGui.begin(HierarchyWindowName);

        if (scene != null) {
            IntBag entitiesIds = scene.getEntitiesIds(TagComponent.class);
            for (int i = 0; i < entitiesIds.size(); i++) {
                drawEntityNode(scene.getEntity(entitiesIds.get(i)));
            }

            if (ImGui.isMouseDown(0) && ImGui.isWindowHovered()) {
                selection.clear();
            }

            if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonRight)) {
                if (ImGui.menuItem("Create Empty Entity")) {
                    scene.createEntity();
                }

                ImGui.endPopup();
            }
        }

        ImGui.end();

        ImGui.begin(PropertiesWindowName);

        Entity selectedEntity = selection.getEntity();
        if (selectedEntity != null) {
            drawEntityProperties(selectedEntity);

            if (ImGui.button("Add Component")) {
                ImGui.openPopup("AddComponent");
            }

            if (ImGui.beginPopup("AddComponent")) {
                for (ComponentEditor<?> componentEditor : componentEditors) {
                    boolean canAdd = !componentEditor.isOn(selectedEntity);
                    if (canAdd && ImGui.menuItem(componentEditor.menuName())) {
                        componentEditor.addTo(selectedEntity);
                        ImGui.closeCurrentPopup();
                    }
                }

                ImGui.endPopup();
            }
        }

        ImGui.end();
    }

    private void drawEntityNode(@NotNull Entity e) {
        String tag = e.getComponent(TagComponent.class).name;
        int flags = ImGuiTreeNodeFlags.OpenOnArrow | (selection.isSelected(e) ? ImGuiTreeNodeFlags.Selected : 0);
        boolean opened = ImGui.treeNodeEx(e.getId(), flags, tag);
        if (ImGui.isItemClicked()) {
            selection.select(e);
        }
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("Delete Entity")) {
                if (selection.isSelected(e)) {
                    selection.clear();
                }
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

        for (ComponentEditor<?> componentEditor : componentEditors) {
            drawComponentProperties(componentEditor, e);
        }
    }

    private void drawTransform(Entity e, TransformComponent transformComponent) {
        drawVector3Editor("Size", transformComponent.size, 1.0f);
        drawVector3Editor("Transform", transformComponent.translate);
        Vector3f rotation = transformComponent.getRotationInDegrees();
        drawVector3Editor("Rotation", rotation);
        transformComponent.setRotationInDegrees(rotation);
    }

    private void drawCamera(Entity e, CameraComponent cameraComponent) {
        ImBoolean imBoolean = new ImBoolean(cameraComponent.primary);
        ImGui.checkbox("Primary Camera", imBoolean);
        cameraComponent.primary = imBoolean.get();

        float[] far = new float[] {cameraComponent.camera.getFar()};
        if (ImGui.dragFloat("Far", far, 0.01f)) {
            cameraComponent.camera.setFar(far[0]);
        }

        float[] near = new float[] {cameraComponent.camera.getNear()};
        if (ImGui.dragFloat("Near", near, 0.01f)) {
            cameraComponent.camera.setNear(near[0]);
        }

        ImInt imInt = new ImInt(cameraComponent.cameraType.ordinal());
        ImGui.listBox("Type", imInt, new String[] {CameraType.Orthographic.name(), CameraType.Perspective.name()});
        if (cameraComponent.cameraType != CameraType.getInstanceFromIndex(imInt.get())) {
            cameraComponent.cameraType = CameraType.getInstanceFromIndex(imInt.get());
            if (cameraComponent.cameraType == CameraType.Perspective) {
                cameraComponent.camera = new PerspectiveCamera(
                        cameraComponent.camera.getAspectRatio(),
                        ((OrthographicCamera) cameraComponent.camera).getZoomLevel() * 9.0f,
                        cameraComponent.camera.getNear(),
                        cameraComponent.camera.getFar());
            } else if (cameraComponent.cameraType == CameraType.Orthographic) {
                cameraComponent.camera = new OrthographicCamera(
                        cameraComponent.camera.getAspectRatio(),
                        ((PerspectiveCamera) cameraComponent.camera).getFov() / 9.0f,
                        cameraComponent.camera.getNear(),
                        cameraComponent.camera.getFar());
            }
        }

        if (cameraComponent.camera instanceof OrthographicCamera orthographicCamera) {
            float[] zoom = new float[] {orthographicCamera.getZoomLevel()};
            if (ImGui.dragFloat("Zoom", zoom, 0.01f)) {
                orthographicCamera.setZoomLevel(zoom[0]);
            }
        } else if (cameraComponent.camera instanceof PerspectiveCamera perspectiveCamera) {
            float[] fov = new float[] {perspectiveCamera.getFov()};
            if (ImGui.dragFloat("Fov", fov, 0.1f)) {
                perspectiveCamera.setFov(fov[0]);
            }
        }
    }

    private void drawSprite(Entity e, SpriteComponent spriteComponent) {
        int type = spriteComponent.isTextured ? 1 : 0;
        ImInt imInt = new ImInt(type);
        ImGui.listBox("Type", imInt, new String[] {"color", "texture"});
        if (type != imInt.get()) {
            if (imInt.get() == 0) {
                spriteComponent.setToColor();
            } else if (imInt.get() == 1) {
                spriteComponent.setToTexture();
            }
        }

        if (!spriteComponent.isTextured) {
            float[] color = new float[] {
                spriteComponent.color.x, spriteComponent.color.y, spriteComponent.color.z, spriteComponent.color.w
            };
            if (ImGui.colorEdit4("Color" + e.getId(), color)) {
                spriteComponent.color.x = color[0];
                spriteComponent.color.y = color[1];
                spriteComponent.color.z = color[2];
                spriteComponent.color.w = color[3];
            }
        } else {
            float[] tilingFactor = new float[] {spriteComponent.tilingFactor};
            if (ImGui.dragFloat("Tiling Factor", tilingFactor, 0.1f)) {
                spriteComponent.tilingFactor = tilingFactor[0];
            }

            if (drawTextureButton(spriteComponent)) {
                fileDialog.openFile(imageFileTypes, file -> useTexture(spriteComponent, file));
            }
            if (ImGui.beginDragDropTarget()) {
                Object payload = ImGui.acceptDragDropPayload("CONTENT_BROWSER_ITEM");
                if (payload instanceof File file && Texture.isImageFile(file.toPath())) {
                    useTexture(spriteComponent, file.toPath());
                }
                ImGui.endDragDropTarget();
            }
        }
    }

    private void useTexture(@NotNull SpriteComponent spriteComponent, @NotNull Path file) {
        try {
            spriteComponent.texture = textures.get(file);
        } catch (TextureLoadException e) {
            YH_LOG_ERROR("{}", e.getMessage());
        }
    }

    private boolean drawTextureButton(@NotNull SpriteComponent spriteComponent) {
        boolean isTextureChosen = spriteComponent.texture != null;
        if (isTextureChosen) {
            return ImGui.imageButton(spriteComponent.texture.getRendererId(), 50, 50);
        }
        return ImGui.button("Choose texture");
    }

    private void drawComponentProperties(@NotNull ComponentEditor<?> componentEditor, @NotNull Entity entity) {
        int flags = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen;
        if (componentEditor.isOn(entity)) {
            String title = componentEditor.title();
            if (ImGui.treeNodeEx(title + entity.getId(), flags, title)) {
                componentEditor.draw(entity);
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

        float[] valueX = new float[] {vector.x};
        if (drawVectorXEditor(valueX, resetValue, multiItemsWidth, buttonSize.x, buttonSize.y)) {
            vector.x = valueX[0];
        }

        float[] valueY = new float[] {vector.y};
        if (drawVectorYEditor(valueY, resetValue, multiItemsWidth, buttonSize.x, buttonSize.y)) {
            vector.y = valueY[0];
        }

        float[] valueZ = new float[] {vector.z};
        if (drawVectorZEditor(valueZ, resetValue, multiItemsWidth, buttonSize.x, buttonSize.y)) {
            vector.z = valueZ[0];
        }

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }

    private boolean drawVectorXEditor(
            float[] value, float resetValue, float width, float buttonWidth, float buttonHeight) {
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
        float[] valueX = new float[] {value[0]};
        if (ImGui.dragFloat("##X", valueX, 0.1f, 0.0f, 0.0f, "%.2f")) {
            value[0] = valueX[0];
            isChanged = true;
        }
        ImGui.popItemWidth();
        return isChanged;
    }

    private boolean drawVectorYEditor(
            float[] value, float resetValue, float width, float buttonWidth, float buttonHeight) {
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
        float[] valueY = new float[] {value[0]};
        if (ImGui.dragFloat("##Y", valueY, 0.1f, 0.0f, 0.0f, "%.2f")) {
            value[0] = valueY[0];
            isChanged = true;
        }
        ImGui.popItemWidth();
        return isChanged;
    }

    private boolean drawVectorZEditor(
            float[] value, float resetValue, float width, float buttonWidth, float buttonHeight) {
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
        float[] valueZ = new float[] {value[0]};
        if (ImGui.dragFloat("##Z", valueZ, 0.1f, 0.0f, 0.0f, "%.2f")) {
            value[0] = valueZ[0];
            isChanged = true;
        }
        ImGui.popItemWidth();
        return isChanged;
    }
}
