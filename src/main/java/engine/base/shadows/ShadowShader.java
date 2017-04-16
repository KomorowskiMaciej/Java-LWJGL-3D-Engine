package engine.base.shadows;

import engine.base.shaders.ShaderProgram;
import engine.base.shaders.UniformFloat;
import engine.base.shaders.UniformMatrix;
import engine.base.shaders.UniformVec2;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class ShadowShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/shadowVertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/shadowFragmentShader.glsl";

    protected UniformMatrix mvpMatrix = new UniformMatrix("mvpMatrix");
    protected UniformVec2 offset = new UniformVec2("offset");
    protected UniformFloat numberOfRows = new UniformFloat("numberOfRows");

    protected ShadowShader() {
        super(VERTEX_FILE, FRAGMENT_FILE, "in_position", "in_textureCoords");
        super.storeAllUniformLocations(
                mvpMatrix,
                offset,
                numberOfRows
        );
    }

    protected void loadMvpMatrix(Matrix4f mvpMatrix) {
        this.mvpMatrix.loadMatrix(mvpMatrix);
    }

    protected void loadOffset(Vector2f offset) {
        this.offset.loadVec2(offset);
    }

    protected void loadNumberOfRows(float numberOfRows) {
        this.numberOfRows.loadFloat(numberOfRows);
    }
}
