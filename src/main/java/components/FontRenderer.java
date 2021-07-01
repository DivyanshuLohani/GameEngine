package components;

public class FontRenderer extends Component {

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void Start() {
        if (gameObject.GetComponent(SpriteRenderer.class) != null){
            System.out.println("Found SpriteRenderer " + gameObject.GetComponent(SpriteRenderer.class));
        }
    }
}
