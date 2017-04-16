package engine.base.shaders;

import engine.base.resourceManagment.containers.file.ResourceFile;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import java.io.BufferedReader;

public abstract class ShaderProgram {

    private int programID;

    public ShaderProgram(String vertexName, String fragmentName, String... inVariables) {

        ResourceFile vertexFile = new ResourceFile(vertexName);
        ResourceFile fragmentFile = new ResourceFile(fragmentName);

        int vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);

        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAttributes(inVariables);
        GL20.glLinkProgram(programID);
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
    }

    protected void storeAllUniformLocations(Uniform... uniforms){
        for(Uniform uniform : uniforms){
            uniform.storeUniformLocation(programID);
        }
        GL20.glValidateProgram(programID);
    }

    protected void storeAllUniformTableLocations(Uniform[]... uniforms){
        for(Uniform[] uniformTable : uniforms){
            int i = 0;
            for (Uniform uniform: uniformTable) {
                uniform.storeUniformLocationAsTableElement(programID,i);
                i++;
            }
        }
        GL20.glValidateProgram(programID);
    }

    private int loadShader(ResourceFile file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = file.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Could not read file.");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader "+ file);
            System.exit(-1);
        }
        return shaderID;
    }

    public void start() {
        GL20.glUseProgram(programID);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        GL20.glDeleteProgram(programID);
    }

    private void bindAttributes(String[] inVariables){
        for(int i=0;i<inVariables.length;i++){
            GL20.glBindAttribLocation(programID, i, inVariables[i]);
        }
    }
}
