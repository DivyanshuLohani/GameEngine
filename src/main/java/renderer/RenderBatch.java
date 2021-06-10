package renderer;

import Util.AssetPool;
import components.SpriteRenderer;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    // Vertex
    // Pos                      // Colors                            // Texture Coordinates       // textureID
    // float, float, float      // float, float, float, float;         float, float                 float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private  final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final  int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final  int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final  int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize) {
        shader = AssetPool.getShader("assets/shaders/default.glsl");
//        shader.compile();
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;


        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void render(){
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // use Shader
        shader.use();
        shader.uploadMatrix4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMatrix4f("uView", Window.getScene().camera().getViewMatrix());

        for(int i=0; i<textures.size(); i++){
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        for(int i=0; i<textures.size(); i++){
            textures.get(i).unbind();
        }
        shader.detach();
    }

    public void addSprite(SpriteRenderer spr){
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;
        if (spr.getTexture() != null){
            if (!textures.contains(spr.getTexture())){
                textures.add(spr.getTexture());
            }
        }

        // add properties
        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }
    }
    private void loadVertexProperties(int index){
        SpriteRenderer sprite = this.sprites[index];

        int offset = index * 4 * VERTEX_SIZE;
        Vector4f color = sprite.getColor();
        Vector2f[] tex_coords = sprite.getTexCoords();

        int texId = 0;
        if (sprite.getTexture() != null){
            for (int i=0; i< textures.size(); i++){
                if (textures.get(i) == sprite.getTexture()){
                    texId = i + 1;
                    break;
                }
            }
        }

        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i =0; i < 4; i++){
            if(i == 1){
                yAdd = 0.0f;
            }else if(i == 2){
                xAdd = 0.0f;
            }else if(i == 3){
                yAdd = 1.0f;
            }
            // Load position
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // Load Color
            vertices[offset + 2] = color.x; // Red
            vertices[offset + 3] = color.y; // green
            vertices[offset + 4] = color.z; // Blue
            vertices[offset + 5] = color.w; // Alpha

            // Load Texture Coordinates
            vertices[offset + 6] = tex_coords[i].x; // Alpha
            vertices[offset + 7] = tex_coords[i].y; // Alpha


            // Load Texture ID
            vertices[offset + 8] = texId; // Alpha

            offset += VERTEX_SIZE;
        }
    }

    public void Start(){
        // Generate and Bind
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate Space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and Upload Indicies Buffer
        int eboId = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the Buffer and vertex pointers;
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    private int[] generateIndices(){
        // 3 Indices in a triangle;
        int[] elements = new int[6 * maxBatchSize];
        for (int i=0; i<maxBatchSize; i++){
            loadElementsIndices(elements, i);
        }
        return elements;
    }

    private void loadElementsIndices(int[] elements, int index){
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // Triangle 1;
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset +2;
        elements[offsetArrayIndex + 2] = offset + 0;
        // Triangle 2;
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }
    public boolean hasRoom(){
        return this.hasRoom;
    }
    public boolean hasTextureRoom(){
        return this.textures.size() < 8;
    }
    public boolean hasTexture(Texture texture){
        return this.textures.contains(texture);
    }
}
