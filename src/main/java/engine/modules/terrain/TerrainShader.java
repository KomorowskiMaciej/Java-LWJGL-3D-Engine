package engine.modules.terrain;

import engine.base.ShaderProgram;
import engine.modules.gameObject.gameObjectComponents.CameraBaseComponent;
import engine.modules.light.Light;
import engine.settings.Config;
import engine.toolbox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;

public class TerrainShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/terrainFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition[];
    private int location_lightColour[];
    private int location_shineDamper;
    private int location_attenuation[];
    private int location_reflectivity;
    private int location_skyColour;
    private int location_backgroundTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;
    private int location_plane;
    private int location_fogDencity;
    private int location_fogGradient;
    private int location_ambientLight;
    private int location_toShadowMapSpace;
    private int location_shadowMap;
    private int location_pcfCount;
    private int location_mapSize;


    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColour = super.getUniformLocation("skyColour");
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
        location_plane = super.getUniformLocation("plane");
        location_fogDencity = super.getUniformLocation("density");
        location_fogGradient = super.getUniformLocation("gradient");
        location_ambientLight = super.getUniformLocation("ambientLight");
        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        location_shadowMap = super.getUniformLocation("shadowMap");
        location_pcfCount = super.getUniformLocation("pcfCount");
        location_mapSize = super.getUniformLocation("mapSize");

        location_lightPosition = new int[Config.getMaxLights()];
        location_lightColour = new int[Config.getMaxLights()];
        location_attenuation = new int[Config.getMaxLights()];
        for (int i = 0; i < Config.getMaxLights(); i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void connectTextureUnits() {
        super.loadInt(location_backgroundTexture, 0);
        super.loadInt(location_rTexture, 1);
        super.loadInt(location_gTexture, 2);
        super.loadInt(location_bTexture, 3);
        super.loadInt(location_blendMap, 4);
        super.loadInt(location_shadowMap, 5);
    }


    public void loadClipPlane(Vector4f clipPlane) {
        super.loadVector(location_plane, clipPlane);
    }

    public void loadSkyColour(Vector3f env_colours) {
        super.loadVector(location_skyColour, env_colours);
    }

    public void loadFogDencity(float fog) {
        super.loadFloat(location_fogDencity, fog);
    }

    public void loadFogGradient(float fog) {
        super.loadFloat(location_fogGradient, fog);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadPcfCount(int i) {
        super.loadInt(location_pcfCount, i);
    }

    public void loadMapSize(float i) {
        super.loadFloat(location_mapSize, i);
    }

    public void loadToShadowSpaceMatrix(Matrix4f matrix) {
        super.loadMatrix(location_toShadowMapSpace, matrix);
    }


    public void loadAmbientLight(float al) {
        super.loadFloat(location_ambientLight, al);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLights(List<Light> lights) {
        for (int i = 0; i < Config.getMaxLights(); i++) {
            if (i < lights.size()) {
                super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
                super.loadVector(location_lightColour[i], lights.get(i).getColour());
                super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
            } else {
                super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
                super.loadVector(location_lightColour[i], new Vector3f(0, 0, 0));
                super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
            }
        }
    }

    public void loadViewMatrix(CameraBaseComponent camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }


}
