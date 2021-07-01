package components;

import Util.Settings;
import engine.GameObject;
import engine.MouseListner;
import engine.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component{
    GameObject holdingObject = null;

    public void pick(GameObject go){
        this.holdingObject = go;
        Window.getScene().AddGameObjectToScene(go);
    }

    public void place(){
        this.holdingObject = null;
    }

    @Override
    public void update(float deltaTime) {
        if (holdingObject != null){
            holdingObject.transform.position.x = MouseListner.getOrthoX();
            holdingObject.transform.position.y = MouseListner.getOrthoY();
            holdingObject.transform.position.x = (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int)(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

            if (MouseListner.getMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT)){
                place();
            }
        }
    }
}
