package renderer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filePath;
    public Shader(String filepath){
        this.filePath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") +6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            index = source.indexOf("#type", eol) +6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if(firstPattern.equals("vertex")){
                vertexSource = splitString[1];
            }else if(firstPattern.equals("fragment")){
                fragmentSource = splitString[1];
            }else{
                throw new IOException("Unexpected Token '" + firstPattern + "' in '" +filepath + "'" );
            }if(secondPattern.equals("vertex")){
                vertexSource = splitString[2];
            }else if(secondPattern.equals("fragment")){
                fragmentSource = splitString[2];
            }else{
                throw new IOException("Unexpected Token '" + secondPattern + "' in '" +filepath + "'" );
            }

        }catch (IOException e){
            e.printStackTrace();
            assert false: "Error cannot open Shader '" + filepath + "'";
        }

//        System.out.println(vertexSource);
//        System.out.println(fragmentSource);
    }
    public void compile(){
        int vertexID, fragmentID;
        // Link and Compile the shader

        // load vertx Shadeer and compile
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // pass to GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error Vertex Shader Not Compiled in '" + filePath + "'");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }
        // load fragment Shader and compile
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // pass to GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error Fragment Shader Not Compiled in '" + filePath + "'");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error Vertex Shader Not Compiled");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }

    }
    public void use(){
        if (!beingUsed) {
            // Bind Shader
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }
    public void detach(){
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMatrix4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    } public void uploadMatrix3f(String varName, Matrix3f mat3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }
    public void uploadVec4f(String varName, Vector4f vec4f){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
       glUniform4f(varLocation, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
    } public void uploadVec3f(String varName, Vector3f vec3f){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
       glUniform3f(varLocation, vec3f.x, vec3f.y, vec3f.z);
    }

    public void uploadFloat(String varname, float val){
        int varLocation = glGetUniformLocation(shaderProgramID, varname);
        use();
        glUniform1f(varLocation, val);
    }
    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }
    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }
    public void uploadIntArray(String varName, int[] array){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, array);
    }
}