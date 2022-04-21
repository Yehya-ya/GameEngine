package Engine.Scenes;

import Engine.Listeners.KeyListener;
import Engine.Renderer.Cube;
import Engine.Renderer.Texture;
import Engine.Window;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class TestScene extends Scene {
    public static final int ID = 101;

    private List<Cube> cubes;

    public TestScene() {
        super();
        cubes = new ArrayList<>();
        File[] textures = new File("assets/textures").listFiles();
        int num = 35;
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                Cube cube = new Cube(this, new Vector3f(num/2 - i, 0.0f,num/2 - j));
                int rand = new Random().nextInt(textures.length);
                cube.setTexture(new Texture(textures[rand].getName(), true));
                cubes.add(cube);
                if (new Random().nextInt(10) < 2) {
                    Cube cube2 = new Cube(this, new Vector3f(num/2 - i, 1.0f,num/2 - j));
                    int rand2 = new Random().nextInt(textures.length);
                    cube2.setTexture(new Texture(textures[rand2].getName(), true));
                    cubes.add(cube2);
                }
            }
        }
    }

    @Override
    public void init() {
        for (Cube cube: cubes){
            cube.init();
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        this.camera.setPos(this.camera.getPos().rotateY(0.01f));
        for (Cube cube: cubes){
            cube.update(dt);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_F1)) {
            boolean isFullScreen = Window.isFullScreen();
            Window.setFullScreen(!isFullScreen);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.close();
        }
        for (Cube cube: cubes){
            cube.render();
        }
    }
}
