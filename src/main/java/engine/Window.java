package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderEngine.DebugDraw;
import renderEngine.FrameBuffer;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window{
    public float r, g, b, a;
    private int width, height;
    private final String title;
    private long glfwWindow;
    private static Window window = null;
    private  static Scene currentScene = null;
    private ImGUILayer imGUILayer;
    private FrameBuffer frameBuffer;
    private Window(){
        width = 1366;
        height = 768;
        title = "Mario";
        r = 1.0f;
        b = 1.0f;
        g = 1.0f;
        a = 1.0f;
    }
    public static void changeScene(int scene){
        switch (scene){
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown Scene " + scene;
                break;
        }
        currentScene.Load();
        currentScene.init();
        currentScene.Start();
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
        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

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

        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) ->{
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });


        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.imGUILayer = new ImGUILayer(glfwWindow);
        this.imGUILayer.initImGui();

        this.frameBuffer = new FrameBuffer(1280, 720);
        glViewport(0, 0, 1280, 720);


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

            DebugDraw.beginFrame();

            this.frameBuffer.bind();
            
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            if (deltaTime >= 0){
                DebugDraw.Draw();
                currentScene.update(deltaTime);
            }
            this.frameBuffer.unbind();
            this.imGUILayer.update(deltaTime, currentScene);

            glfwSwapBuffers(glfwWindow); // swap the color buffers


            endTime = (float) glfwGetTime(); // get the end of frame
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }
        currentScene.saveExit();
    }
    public static int getWidth(){
        return get().width;
    }
    public static int getHeight(){
        return get().height;
    }

    public static void setWidth(int width) {
        get().width = width;
    }

    public static void setHeight(int height) {
        get().height = height;
    }

    public static FrameBuffer getFrameBuffer(){
        return get().frameBuffer;
    }

    public static float getTargetAspectRatio(){
        return 16.0f / 9.0f;
    }
}