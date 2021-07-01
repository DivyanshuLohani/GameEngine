package components;

import imgui.ImGui;
import engine.GameObject;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {
    private static int ID_COUNT = 0;
    private int uid = -1;
    public transient GameObject gameObject = null;
    public void update(float deltaTime){

    }
    public void Start(){

    }
    public void imgui(){
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient) continue;
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate){
                    field.setAccessible(true);
                }

                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class){
                    int val =  (int)value;
                    int[] imInt = {val};
                    if (ImGui.dragInt(name + ": ", imInt)){
                        field.set(this, imInt[0]);
                    }
                }else if (type == float.class){
                    float val =  (float)value;
                    float[] imfloat = {val};
                    if (ImGui.dragFloat(name + ": ", imfloat)){
                        field.set(this, imfloat[0]);
                    }
                }else if (type == boolean.class){
                    boolean val =  (boolean)value;
                    if (ImGui.checkbox(name + ": ", val)){
                        field.set(this, !val);
                    }
                }else if (type == Vector3f.class){
                    Vector3f val =  (Vector3f) value;
                    float[] vec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", vec)){
                        val.set(vec[0], vec[1], vec[2]);
                    }
                }else if (type == Vector4f.class){
                    Vector4f val =  (Vector4f) value;
                    float[] vec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name + ": ", vec)){
                        val.set(vec[0], vec[1], vec[2], vec[4]);
                    }
                }


                if (isPrivate){
                    field.setAccessible(false);
                }
            }
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void generateId(){
        if (this.uid == -1){
            this.uid = ID_COUNT++;
        }
    }
    public int getUid(){
        return this.uid;
    }
    public static void init(int maxID){
        ID_COUNT = maxID;
    }

}
