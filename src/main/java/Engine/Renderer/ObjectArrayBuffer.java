package Engine.Renderer;

import Engine.Scenes.Scene;
import com.mokiat.data.front.parser.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL45.*;

public abstract class ObjectArrayBuffer {
    protected final ShaderProgram shaderProgram;
    private final Scene scene;
    protected int vaoID, vboID;
    protected float[] data;
    protected Matrix4f transformationMatrix;
    protected Texture texture;

    public ObjectArrayBuffer(Scene scene, String vertexShader, String fragmentShader, String objectFileName, String textureFileName) {
        this.scene = scene;
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        transformationMatrix = new Matrix4f().identity();
        texture = new Texture(textureFileName, true);
        String path = "assets/objects/" + objectFileName + ".obj";
        try (InputStream in = new FileInputStream(path)) {
            // Create an OBJParser and parse the resource
            final IOBJParser parser = new OBJParser();
            final OBJModel model = parser.parse(in);

            List<Vertex> vertices = new ArrayList<>();

            for (OBJFace face : model.getObjects().get(0).getMeshes().get(0).getFaces()) {
                List<OBJDataReference> referenceList = face.getReferences();
                if (referenceList.size() == 3) {
                    vertices.addAll(excludeDataFromReferences(referenceList, model));
                } else if (referenceList.size() == 4) {
                    vertices.addAll(excludeDataFromReferences(referenceList.subList(0, 3), model));
                    referenceList.remove(1);
                    vertices.addAll(excludeDataFromReferences(referenceList, model));
                }
            }
            this.data = new float[vertices.size() * Vertex.TotalSize];
            for (int i = 0; i < vertices.size(); i++) {
                int index = i * Vertex.TotalSize;
                this.data[index] = vertices.get(i).position.x;
                this.data[index + 1] = vertices.get(i).position.y;
                this.data[index + 2] = vertices.get(i).position.z;
                this.data[index + 3] = vertices.get(i).normal.x;
                this.data[index + 4] = vertices.get(i).normal.y;
                this.data[index + 5] = vertices.get(i).normal.z;
                this.data[index + 6] = vertices.get(i).textureCoords.x;
                this.data[index + 7] = vertices.get(i).textureCoords.y;
            }

        } catch (FileNotFoundException e) {
            assert false : "file does not exist.";
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void init() {
        shaderProgram.compileAndLink();
        texture.init();
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, Vertex.PositionSize, GL_FLOAT, false, Vertex.TotalSize * Float.BYTES, Vertex.PositionOffset);
        glEnableVertexAttribArray(0);
        // Enable the buffer attribute pointers
        glVertexAttribPointer(1, Vertex.NormalSize, GL_FLOAT, false, Vertex.TotalSize * Float.BYTES, Vertex.NormalOffset);
        glEnableVertexAttribArray(1);
        // Enable the buffer attribute pointers
        glVertexAttribPointer(2, Vertex.TextureCoordsSize, GL_FLOAT, false, Vertex.TotalSize * Float.BYTES, Vertex.TextureCoordsOffset);
        glEnableVertexAttribArray(2);
    }

    abstract public void update(float dt);

    public void render() {
        shaderProgram.bind();
        shaderProgram.setMatrix4f("projectionMatrix", scene.getProjectionMatrix());
        shaderProgram.setMatrix4f("viewMatrix", scene.getViewMatrix());
        shaderProgram.setMatrix4f("transformationMatrix", transformationMatrix);
        texture.bind(shaderProgram);

        glBindVertexArray(vaoID);
        glDrawArrays(GL_TRIANGLES, 0, data.length);
        shaderProgram.unbind();
    }

    public void destroy() {
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
        shaderProgram.destroy();
        texture.destroy();
    }

    private List<Vertex> excludeDataFromReferences(List<OBJDataReference> referenceList, OBJModel model) {
        List<Vertex> vertices = new ArrayList<>();
        for (OBJDataReference reference : referenceList) {
            final OBJVertex vertex = model.getVertex(reference);
            Vertex vertex1 = new Vertex();
            vertex1.position = new Vector3f(vertex.x,vertex.y, vertex.z);

            if (reference.hasNormalIndex()) {
                final OBJNormal normal = model.getNormal(reference);
                vertex1.normal = new Vector3f(normal.x, normal.y, normal.z);
            }else {
                vertex1.normal = new Vector3f(0.0f,0.0f, 0.0f);
            }
            if (reference.hasTexCoordIndex()) {
                final OBJTexCoord texCoord = model.getTexCoord(reference);
                vertex1.textureCoords = new Vector2f(texCoord.u, texCoord.v);
            }else{
                vertex1.textureCoords = new Vector2f(0.0f,0.0f);
            }
            vertices.add(vertex1);
        }
        return vertices;
    }

    static private class Vertex {
        private static final int PositionSize = 3;
        private static final int NormalSize = 3;
        private static final int TextureCoordsSize = 2;
        private static final int TotalSize = PositionSize + NormalSize + TextureCoordsSize;
        private static final int PositionOffset = 0;
        private static final int NormalOffset =  PositionSize * Float.BYTES;
        private static final int TextureCoordsOffset = (PositionSize + NormalSize) * Float.BYTES;

        Vector3f position;
        Vector3f normal;
        Vector2f textureCoords;

        public Vertex () {
            position = new Vector3f();
            normal = new Vector3f();
            textureCoords = new Vector2f();
        }
    }
}
