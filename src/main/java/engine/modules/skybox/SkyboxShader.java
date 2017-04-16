package engine.modules.skybox;

import engine.base.shaders.ShaderProgram;
import engine.base.shaders.UniformFloat;
import engine.base.shaders.UniformInt;
import engine.base.shaders.UniformMatrix;
import engine.base.shaders.UniformVec3;
import engine.base.gameObject.gameObjectComponents.CameraBaseComponent;
import engine.base.gameWindow.Window;
import engine.settings.Config;
import engine.toolbox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


public class SkyboxShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/skyboxVertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/skyboxFragmentShader.glsl";

    private UniformMatrix   projectionMatrix = new UniformMatrix("projectionMatrix");
    private UniformMatrix   viewMatrix = new UniformMatrix("viewMatrix");
    private UniformVec3     fogColour = new UniformVec3("fogColour");
    private UniformInt      cubeMap = new UniformInt("cubeMap");
    private UniformInt      cubeMap2 = new UniformInt("cubeMap2");
    private UniformFloat    blendFactor = new UniformFloat("blendFactor");

    private float rotation = 0;

    public SkyboxShader() {

        super(VERTEX_FILE, FRAGMENT_FILE, "position");
        super.storeAllUniformLocations(
                projectionMatrix,
                viewMatrix,
                fogColour,
                cubeMap,
                cubeMap2,
                blendFactor
        );
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        projectionMatrix.loadMatrix(matrix);
    }

    public void loadViewMatrix(CameraBaseComponent camera) {
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        rotation += Config.getSkyboxRotateSpeed() * Window.getDeltaTime();
        Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
        viewMatrix.loadMatrix(matrix);
    }

    public void loadFogColour(Vector3f env_colours) {
        fogColour.loadVec3(env_colours);
    }

    public void connectTextureUnits() {
        cubeMap.loadInt(0);
        cubeMap2.loadInt(1);
    }

    public void loadBlendFactor(float blend) {
        blendFactor.loadFloat(blend);
    }

}