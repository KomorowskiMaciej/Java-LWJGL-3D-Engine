package Engine.modules.shadows;

import Engine.base.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class ShadowShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/shadowVertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/shadowFragmentShader.glsl";

    private int location_mvpMatrix;
    private int location_offset;
    private int location_numberOfRows;

    protected ShadowShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_mvpMatrix = super.getUniformLocation("mvpMatrix");
        location_offset = super.getUniformLocation("offset");
        location_numberOfRows = super.getUniformLocation("numberOfRows");

    }

    protected void loadMvpMatrix(Matrix4f mvpMatrix) {
        super.loadMatrix(location_mvpMatrix, mvpMatrix);
    }

    protected void loadOffset(Vector2f offset) {
        super.load2DVector(location_offset, offset);
    }

    protected void loadNumberOfRows(float numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "in_textureCoords");
    }

}
