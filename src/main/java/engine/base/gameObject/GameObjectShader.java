package engine.base.gameObject;

import engine.base.shaders.ShaderProgram;
import engine.base.shaders.*;
import engine.base.gameObject.gameObjectComponents.CameraBaseComponent;
import engine.modules.light.Light;
import engine.settings.Config;
import engine.toolbox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;

public class GameObjectShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/fragmentShader.glsl";

    protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    protected UniformVec3   lightPosition[];
    protected UniformVec3   lightColour[];
    protected UniformVec3   attenuation[];
    protected UniformFloat   shineDamper = new UniformFloat("shineDamper");
    protected UniformFloat   reflectivity = new UniformFloat("reflectivity");
    protected UniformBoolean useFakeLighting = new UniformBoolean("useFakeLighting");
    protected UniformVec3   skyColour = new UniformVec3("skyColour");
    protected UniformFloat  numberOfRows = new UniformFloat("numberOfRows");
    protected UniformVec2   offset = new UniformVec2("offset");
    protected UniformVec4   plane = new UniformVec4("plane");
    protected UniformFloat  fogDencity = new UniformFloat("density");
    protected UniformFloat  fogGradient = new UniformFloat("gradient");
    protected UniformFloat  ambientLight = new UniformFloat("ambientLight");
    protected UniformMatrix toShadowMapSpace = new UniformMatrix("toShadowMapSpace");
    protected UniformSampler shadowMap = new UniformSampler("shadowMap");
    protected UniformInt    pcfCount =new UniformInt("pcfCount");
    protected UniformFloat  mapSize = new UniformFloat("mapSize");

    public GameObjectShader() {
        super(VERTEX_FILE, FRAGMENT_FILE, "position", "textureCoordinates", "normal");
        lightPosition = new UniformVec3[Config.getMaxLights()];
        lightColour = new UniformVec3[Config.getMaxLights()];
        attenuation = new UniformVec3[Config.getMaxLights()];

        for (int i = 0; i < Config.getMaxLights(); i++) {
            lightPosition[i] = new UniformVec3("lightPosition");
            lightColour[i] = new UniformVec3("lightColour");
            attenuation[i] = new UniformVec3("attenuation");
        }

        super.storeAllUniformLocations(
                transformationMatrix,
                projectionMatrix,
                viewMatrix,
                shineDamper,
                reflectivity,
                useFakeLighting,
                skyColour,
                numberOfRows,
                offset,
                plane,
                fogDencity,
                fogGradient,
                ambientLight,
                toShadowMapSpace,
                shadowMap,
                pcfCount,
                mapSize
        );
        storeAllUniformTableLocations(
                lightPosition,
                lightColour,
                attenuation
        );
        connectTextureUnits();
    }


    public void connectTextureUnits() {
        start();
        shadowMap.loadTexUnit(5);
        stop();
    }

    public void loadClipPlane(Vector4f plane) {
        this.plane.loadVec4(plane);
    }

    public void loadNumberOfRows(int numberOfRows) {
        this.numberOfRows.loadFloat( numberOfRows);
    }

    public void loadOffset(float x, float y) {
        offset.loadVec2(new Vector2f(x, y));
    }

    public void loadPcfCount(int i) {
        pcfCount.loadInt(i);
    }

    public void loadMapSize(float i) {
        mapSize.loadFloat(i);
    }

    public void loadToShadowSpaceMatrix(Matrix4f matrix) {
        toShadowMapSpace.loadMatrix(matrix);
    }

    public void loadSkyColour(Vector3f env_colours) {
        skyColour.loadVec3(env_colours);
    }

    public void loadAmbientLight(float al) {

        ambientLight.loadFloat(al);
    }

    public void loadFakeLightingVariable(boolean useFake) {
        useFakeLighting.loadBoolean( useFake);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        shineDamper.loadFloat(damper);
        this.reflectivity.loadFloat(reflectivity);
    }

    public void loadFogDencity(float fog) {
        fogDencity.loadFloat(fog);
    }

    public void loadFogGradient(float fog) {

        fogGradient.loadFloat(fog);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        transformationMatrix.loadMatrix(matrix);
    }

    public void loadLights(List<Light> lights) {
        for (int i = 0; i < Config.getMaxLights(); i++) {
            if (i < lights.size()) {
                lightPosition[i].loadVec3(lights.get(i).getPosition());
                lightColour[i].loadVec3(lights.get(i).getColour());
                attenuation[i].loadVec3(lights.get(i).getAttenuation());
            } else {
                lightPosition[i].loadVec3(new Vector3f(0, 0, 0));
                lightColour[i].loadVec3(new Vector3f(0, 0, 0));
                attenuation[i].loadVec3(new Vector3f(1, 0, 0));
            }
        }
    }

    public void loadViewMatrix(CameraBaseComponent camera) {
        viewMatrix.loadMatrix(Maths.createViewMatrix(camera));
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        projectionMatrix.loadMatrix(projection);
    }


}
