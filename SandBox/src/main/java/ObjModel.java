import GameEngine.Engine.Renderer.Buffer.BufferLayout;
import GameEngine.Engine.Renderer.Buffer.IndexBuffer;
import GameEngine.Engine.Renderer.Buffer.VertexBuffer;
import GameEngine.Engine.Renderer.VertexArray;
import GameEngine.Engine.Utils.Resources;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjModel {
    private static final int floatsPerVertex = 8;
    private static final int positionReference = 0;
    private static final int textureCoordinateReference = 1;
    private static final int normalReference = 2;

    private final List<float[]> positions = new ArrayList<>();
    private final List<float[]> textureCoordinates = new ArrayList<>();
    private final List<float[]> normals = new ArrayList<>();
    private final Map<String, Integer> vertexIndexByFaceCorner = new HashMap<>();
    private final List<Float> vertices = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();

    private ObjModel() {}

    public static ObjModel fromResource(String resourceName) {
        ObjModel model = new ObjModel();
        Resources.readText(resourceName).lines().forEach(model::readLine);
        return model;
    }

    public VertexArray createVertexArray() {
        VertexBuffer vertexBuffer = VertexBuffer.create(toFloatArray(vertices));
        vertexBuffer.setLayout(new BufferLayout()
                .addBufferElement(BufferLayout.ShaderDataType.Float3, "aPos", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float3, "aNormal", false)
                .addBufferElement(BufferLayout.ShaderDataType.Float2, "aTexCoords", false));

        VertexArray vertexArray = VertexArray.create();
        vertexArray.addVertexBuffer(vertexBuffer);
        vertexArray.setIndexBuffer(
                IndexBuffer.create(indices.stream().mapToInt(Integer::intValue).toArray()));
        return vertexArray;
    }

    private void readLine(String line) {
        String[] parts = line.trim().split("\\s+");
        switch (parts[0]) {
            case "v" -> positions.add(readFloats(parts, 3));
            case "vt" -> textureCoordinates.add(readFloats(parts, 2));
            case "vn" -> normals.add(readFloats(parts, 3));
            case "f" -> readFace(parts);
            default -> {}
        }
    }

    private void readFace(String[] parts) {
        for (int corner = 2; corner + 1 < parts.length; corner++) {
            indices.add(vertexIndexOf(parts[1]));
            indices.add(vertexIndexOf(parts[corner]));
            indices.add(vertexIndexOf(parts[corner + 1]));
        }
    }

    private int vertexIndexOf(String faceCorner) {
        return vertexIndexByFaceCorner.computeIfAbsent(faceCorner, this::addVertex);
    }

    private int addVertex(String faceCorner) {
        String[] references = faceCorner.split("/");
        int vertexIndex = vertices.size() / floatsPerVertex;
        addAll(referencedValue(positions, references, positionReference, 3));
        addAll(referencedValue(normals, references, normalReference, 3));
        addAll(referencedValue(textureCoordinates, references, textureCoordinateReference, 2));
        return vertexIndex;
    }

    private static float[] referencedValue(List<float[]> values, String[] references, int reference, int size) {
        boolean isMissing = references.length <= reference || references[reference].isEmpty();
        if (isMissing) {
            return new float[size];
        }
        int objIndex = Integer.parseInt(references[reference]);
        boolean countsFromEnd = objIndex < 0;
        return values.get(countsFromEnd ? values.size() + objIndex : objIndex - 1);
    }

    private void addAll(float[] values) {
        for (float value : values) {
            vertices.add(value);
        }
    }

    private static float[] readFloats(String[] parts, int count) {
        float[] values = new float[count];
        for (int i = 0; i < count; i++) {
            values[i] = Float.parseFloat(parts[i + 1]);
        }
        return values;
    }

    private static float[] toFloatArray(List<Float> values) {
        float[] array = new float[values.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = values.get(i);
        }
        return array;
    }
}
