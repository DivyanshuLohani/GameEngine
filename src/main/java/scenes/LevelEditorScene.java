package scenes;

import Util.AssetPool;
import components.*;
import engine.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

public class LevelEditorScene extends  Scene {

    GameObject obj1;
    SpriteSheet sprites;
    GameObject levelEditorObject = new GameObject("Level Editor", new Transform(), 0);
    @Override
    public void init() {

        levelEditorObject.AddComponent(new MouseControls());
        levelEditorObject.AddComponent(new GridLines());

        loadResources();
        this.camera = new Camera(new Vector2f());
        sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        if (levelLoaded){
            activeGameObject = (gameObjects.get(0));
            return;
        }
    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.getTexture("assets/images/testImage.png");

        for (GameObject g: gameObjects) {
            SpriteRenderer spr = g.GetComponent(SpriteRenderer.class);
            if (spr != null){
                if (spr.getTexture() != null) {
                     spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
            }
        }
    }


    @Override
    public void update(float deltaTime) {
        levelEditorObject.Update(deltaTime);
        for (GameObject go : this.gameObjects){
            go.Update(deltaTime);
        }
        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth()  * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int id = sprite.getTexID();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)){
                GameObject object = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                // attach to cursor
                levelEditorObject.GetComponent(MouseControls.class).pick(object);
            }
            ImGui.popID();
            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if (i + 1 < sprites.size() && nextButtonX2 < windowX2){
                ImGui.sameLine();
            }

        }

        ImGui.end();
    }
}
