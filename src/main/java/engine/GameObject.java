package engine;

import components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uid = -1;

    private String name;
    private List<Component> components;
    public Transform transform;
    private int zIndex;

    public GameObject(String name, Transform transform, int zIndex){
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
        this.transform.gameObject = this;
        this.zIndex = zIndex;
        this.uid = ID_COUNTER++;
    }


    public <T extends Component> T GetComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error Couldn't cast";
                }
            }
        }
        return null;
    }

    public <T extends Component> void RemoveComponent(Class<T> componentClass){
        for (int i = 0; i < components.size(); i++){
            Component c = components.get(i);
                if (componentClass.isAssignableFrom(c.getClass())) {
                    components.remove(i);
                    return;
                }
            }
        }


    public void AddComponent(Component c){
        c.generateId();
        this.components.add(c);
        c.gameObject = this;
    }

    public void Update(float deltaTime){
        for (Component component : components) {
            component.update(deltaTime);
        }
    }
    public void Start(){
        for (Component component : components) {
            component.Start();
        }
    }
    public void imgui(){
        for (Component c: components){
            c.imgui();
        }
    }

    public int getzIndex() {
        return zIndex;
    }
    public static void init(int maxID){
        ID_COUNTER = maxID;
    }

    public int getUid() {
        return this.uid;
    }

    public List<Component> getAllComponents(){
        return this.components;
    }
}
