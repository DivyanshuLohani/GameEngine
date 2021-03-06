package components;

import jade.Component;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;

public class SpriteRenderer extends Component {

    private Vector4f color ;
    private Sprite sprite;

    public SpriteRenderer(Vector4f color){
        this.color = color;
        this.sprite = new Sprite(null);
    }
    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
        this.color = new Vector4f(1f, 1f, 1f,1f);
    }


    @Override
    public void Start() {

    }

    @Override
    public void update(float deltaTime) {

    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }
    public Vector4f getColor(){
        return  this.color;
    }

}
