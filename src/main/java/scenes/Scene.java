package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializzer;
import imgui.ImGui;
import engine.Camera;
import engine.GameObject;
import engine.GameObjectDeserializer;
import renderEngine.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    protected boolean levelLoaded = false;

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

    public void sceneImgui(){
        if (activeGameObject != null){
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }
        imgui();
    }
    public void imgui(){

    }

    public void saveExit(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializzer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        try {
            FileWriter writer = new FileWriter("Level.jscene");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void Load(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializzer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("Level.jscene")));
        }catch (IOException e){
            e.printStackTrace();
        }

        if (!inFile.equals("")){
            int maxGoId = -1; // GameObjeckt ID
            int maxCompId= -1; // Component Id
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i=0; i < objs.length; i++){
                AddGameObjectToScene(objs[i]);
                for (Component c: objs[i].getAllComponents()){
                    if(c.getUid() > maxCompId){
                        maxCompId = c.getUid();
                    }
                    if (objs[i].getUid() > maxGoId){
                        maxGoId = objs[i].getUid();
                    }
                }
            }
            maxGoId++;
            maxCompId++;
            // System.out.println(maxCompId);
            // System.out.println(maxGoId);
            GameObject.init(maxGoId);
            Component.init(maxCompId);
            this.levelLoaded = true;
        }
    }
}
