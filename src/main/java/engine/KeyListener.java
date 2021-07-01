package engine;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instace;
    private boolean keyPrssed[] = new boolean[350];

    private KeyListener(){

    }

    private static  KeyListener get(){
        if (KeyListener.instace == null){
            KeyListener.instace = new KeyListener();
        }
        return KeyListener.instace;
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int mods){
        if (action == GLFW_PRESS){
            get().keyPrssed[key] = true;
        }else if (action == GLFW_RELEASE){
            get().keyPrssed[key] = false;
        }
    }

    public static boolean isKeyPressed(int key){
//        if (key < get().keyPrssed.length) {
            return get().keyPrssed[key];
//        }else{
//            return false;
//        }
    }

}
