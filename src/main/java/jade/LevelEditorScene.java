package jade;

import Util.AssetPool;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends  Scene {

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());

        SpriteSheet sprites = AssetPool.getSpriteSheet("assets/images/spriteSheet.png");

        GameObject obj1 = new GameObject("Mario", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.AddComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.AddGameObjectToScene(obj1);
        GameObject obj2 = new GameObject("Gumba", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.AddComponent(new SpriteRenderer(sprites.getSprite(10)));
        this.AddGameObjectToScene(obj2);

    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spriteSheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheet.png"), 16, 16, 26, 0));
        }

    @Override
    public void update(float deltaTime) {
        System.out.println(1/deltaTime);
        for (GameObject go : this.gameObjects){
            go.Update(deltaTime);
        }
        this.renderer.render();
    }

}
