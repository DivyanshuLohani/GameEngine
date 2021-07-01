package renderEngine;

import Util.AssetPool;
import Util.JMath;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int MAX_LINES = 500;

    private static List<Line2D> lines = new ArrayList<>();
    // 6 floats per vertex and 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debug.glsl");

    private static int vaoID;
    private static int vboID;
//    private static boolean stated = false;

    private static boolean started = false;

    public static void start() {
        // generate Vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(2f);
    }

    public static void beginFrame() {
        if (!started){
            start();
            started = true;
        }
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0){
                lines.remove(i);
                i--;
            }
        }
    }
    public static void Draw() {
        if(lines.size() <= 0) return;
        int index = 0;
        for(Line2D line : lines){
            for (int i = 0; i < 2; i++) {
                Vector2f pos = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                // vertex loading
                vertexArray[index] = pos.x;
                vertexArray[index + 1] = pos.y;
                vertexArray[index + 2] = -10.0f;

                // color Loading
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;
            }
        }
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        shader.use();
        shader.uploadMatrix4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMatrix4f("uView", Window.getScene().camera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();


    }
    // =====================================================
    //                      Lines 2D
    // =====================================================
    public static void addLine2D(Vector2f from, Vector2f to){
        // TODO ADD Constant COLORS
        addLine2D(from, to, new Vector3f(0, 1, 0), 1);
    }
    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color){
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifeTime){
        lifeTime = lifeTime * 60;
        if (lines.size() >= MAX_LINES) return;
        DebugDraw.lines.add(new Line2D(from, to, color, lifeTime));
    }

    // =====================================================
    //                      Box 2D
    // =====================================================
    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation, Vector3f color, int lifeTime){
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimension).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimension).mul(0.5f));
        Vector2f[] vertices = {
                new Vector2f(min.x, min.y),
                new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y),
                new Vector2f(max.x, min.y)
        };
        if (rotation != 0.0f){
            for(Vector2f vert : vertices){
                JMath.rotate(vert, rotation, center);
            }
        }
        addLine2D(vertices[0], vertices[1], color, lifeTime);
        addLine2D(vertices[0], vertices[3], color, lifeTime);
        addLine2D(vertices[1], vertices[2], color, lifeTime);
        addLine2D(vertices[2], vertices[3], color, lifeTime);
    }
    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation){
        DebugDraw.addBox2D(center, dimension, rotation, new Vector3f(0, 1, 0), 1);
    }
    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation, Vector3f color) {
        DebugDraw.addBox2D(center, dimension, rotation, color, 1);
    }

    // =====================================================
    //                      Circle 2D
    // =====================================================

    public static void addCircle2D(Vector2f center, float radius, Vector3f color, int lifeTime) {
        Vector2f[] points = new Vector2f[15];
        int increment = 360/points.length;
        int currentAngle  = 0;

        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(radius, 0);
            JMath.rotate(tmp, currentAngle, new Vector2f());
            points[i] = new Vector2f(tmp).add(center);

            if (i > 0){
                addLine2D(points[i - 1], points[i], color, lifeTime);
            }
            currentAngle += increment;
        }
        addLine2D(points[points.length - 1], points[0], color, lifeTime);

    }
    public static void addCircle2D(Vector2f center, float radius){
        DebugDraw.addCircle2D(center, radius, new Vector3f(0, 1, 0), 1);
    }
    public static void addCircle2D(Vector2f center, float radius, Vector3f color){
        DebugDraw.addCircle2D(center, radius, color,  1);
    }

}

