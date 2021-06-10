package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window{
    public float r, g, b, a;
    private boolean fadeToblack = false;
    private int width, height;
    private String title;
    private long glfwWindow;
    private static Window window = null;
    private  static Scene currentScene = null;

    private Window(){
        width = 512;
        height = 512;
        title = "Mario";
        r = 0.0f;
        b = 0.0f;
        g = 0.0f;
        a = 1.0f;
    }
    public static void changeScene(int scene){
        switch (scene){
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.Start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.Start();
                break;
            default:
                assert false : "Unknown Scene " + scene;
                break;
        }
    }
    public static Scene getScene(){
        return get().currentScene;
    }

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }
        return  Window.window;
    }

    public void run(){
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");

        init();
        loop();
    }

    public void  init(){
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
         glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE); // maximizes the window bit glitchy on excellence

        // Create the window
        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if ( glfwWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // register Mouse events
            // mouse position callback
        glfwSetCursorPosCallback(glfwWindow, MouseListner::mousePosCallback);
            // mouse button callback
        glfwSetMouseButtonCallback(glfwWindow, MouseListner::mouseButtonCallback);
            // mouse Scroll callback
        glfwSetScrollCallback(glfwWindow, MouseListner::scrollCallback);

        // register Key Events
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);


        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();
        Window.changeScene(0);
    }
    public void loop(){
        float beginTime= (float)glfwGetTime(); //
        float endTime;
        float deltaTime = -1.0f;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(glfwWindow) ) {
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer


            if (deltaTime >= 0){
                currentScene.update(deltaTime);
            }
//            if (fadeToblack){
//                r = Math.max(r - 0.01f, 0.6f);
//                g = Math.max(g - 0.01f, 0.3f);
//                b = Math.max(b - 0.01f, 0.7f);
//            }
//
//            // random fun
//            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
//                fadeToblack = true;
//            }
//            if (MouseListner.getMouseButtonPress(0)){
//                System.out.println("Noob left click hai");
//            }
//            if (KeyListener.isKeyPressed(GLFW_KEY_G)){
//                System.out.println(MouseListner.getScrollX());
//                System.out.println(MouseListner.getScrollY());
//            }

            glfwSwapBuffers(glfwWindow); // swap the color buffers


            endTime = (float) glfwGetTime(); // get the end of frame
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }
    }
}