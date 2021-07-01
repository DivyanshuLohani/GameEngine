package components;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class RigidBody extends Component {
    private int colliderType = 0;
    private float gravity = 5;
    private Vector3f velocity = new Vector3f(0, 0.5f, 0);
    private transient Vector4f tmp = new Vector4f(0.2f, 0.32f, 0.6f, 0);
}
