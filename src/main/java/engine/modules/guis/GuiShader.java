package engine.modules.guis;

import engine.base.shaders.ShaderProgram;
import engine.base.shaders.UniformMatrix;
import org.lwjgl.util.vector.Matrix4f;

public class GuiShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/guiVertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/guiFragmentShader.glsl";

    private UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE, "position");
        super.storeAllUniformLocations(transformationMatrix);
    }

    public void loadTransformation(Matrix4f matrix) {
        transformationMatrix.loadMatrix(matrix);
    }
}
