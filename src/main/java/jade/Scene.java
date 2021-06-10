package jade;

import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    public Camera camera(){
        return this.camera;
    }

    public Scene(){

    }
    public void init (){

    }

    public void Start(){
        for (GameObject go : gameObjects){
            go.Start();
            this.renderer.add(go);
        }isRunning = true;
    }

    public void AddGameObjectToScene(GameObject go){
        if (!isRunning){
            gameObjects.add(go);
        }else{
            gameObjects.add(go);
            go.Start();
            this.renderer.add(go);
        }
    }
    public abstract void update(float deltaTime);


}
