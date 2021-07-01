package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListner {
    private static MouseListner instance = null;
    private double scrollX, scrollY;

    private  double xPos, yPos, lastY, lastX;
    private boolean mouseButtonPress[] = new boolean[3];
    private boolean isDragging;

    private Vector2f gameViewPortPos = new Vector2f();
    private Vector2f gameViewPortSize = new Vector2f();

    private MouseListner(){
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
    }
    public static MouseListner get(){
        if (MouseListner.instance == null){
            MouseListner.instance = new MouseListner();
        }
        return MouseListner.instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos)
    {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
        get().isDragging = get().mouseButtonPress[0] || get().mouseButtonPress[1] || get().mouseButtonPress[2];
    }

    public  static void mouseButtonCallback(long window, int button, int action, int mods)
    {
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonPress.length) {
                get().mouseButtonPress[button] = true;
            }else{
                System.out.println(button);
            }
        }else if(action == GLFW_RELEASE){
            if (button < get().mouseButtonPress.length) {
                get().mouseButtonPress[button] = false;
                get().isDragging = false;
            }
        }
    }
    public static void scrollCallback(long window, double xoffset, double yoffset)
    {
        get().scrollX = xoffset;
        get().scrollY = yoffset;
    }

    public static  void endFrame(){
        get().scrollY = 0;
        get().scrollY = 0;
        get().lastY = get().xPos;
        get().lastX = get().yPos;
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static float getxPos() {
        return (float)get().xPos;
    }

    public static float getOrthoX(){
        float currentX = getxPos() - get().gameViewPortPos.x;
        currentX = (currentX / (float)get().gameViewPortSize.x) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);

        tmp.mul(viewProjection);
        currentX = tmp.x;
        return currentX;
    }

    public  static float getOrthoY(){
        float currentY = getyPos() - get().gameViewPortPos.y;
        currentY = -((currentY / (float)get().gameViewPortSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);
        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);

        tmp.mul(viewProjection);
        currentY = tmp.y;
        return currentY;
    }

    public static float getyPos() {
        return (float)get().yPos;
    }

    public static float getDY() {
        return (float) (get().lastY - get().yPos);
    }

    public static float getDX() {
        return (float) (get().lastX - get().xPos);
    }

    public static boolean getMouseButtonPress(int button) {
        if (button < get().mouseButtonPress.length) {
            return get().mouseButtonPress[button];
        }else{
            return false;
        }
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewPortPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewPortSize.set(gameViewportSize);
    }
}
