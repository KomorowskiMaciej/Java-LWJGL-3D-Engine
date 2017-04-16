package engine.settings;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by Maciek on 14.07.2016.
 */
public class Config {


    private static final String GAME_TITLE = "Test game";
    private static final String RES_PATH = "res/";


    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;


    private static final int FPS_CAP = 120;

    private static final float FOV = 70;                                                                                  // Angle of camera view
    private static final float NEAR_PLANE = 0.1f;                                                                         // Near culling plane
    private static final float FAR_PLANE = 1000;                                                                           // Far culling plane
    private static final int MAX_LIGHTS = 4;
    private static final float MOUSE_PICKER_ROOT_OBJECT_RANGE = 5;
    private static final float LOD_BIAS = 0f;
    private static final Vector3f ENV_RGB = new Vector3f(.3f, .2f, .4f);                                                  // enviroment colours (fog, Engine.modules.skybox mixine, ambient)
    private static final float FOG_DENCITY = 0.007f;                                                                      // dencity of fog
    private static final float FOG_GRADIENT = 4f;                                                                           // fog spread
    private static final float SKYBOX_SIZE = 500f;                                                                        // number of px in Engine.modules.skybox
    private static final float SKYBOX_ROTATE_SPEED = 0.6f;
    private static final float SHADOW_OFFSET = 30;
    private static final Vector4f SHADOW_UP = new Vector4f(0, 1, 0, 0);


    //shadows
    private static final Vector4f SHADOW_FORWARD = new Vector4f(0, 0, -1, 0);
    private static final float SHADOW_DISTANCE = 120;
    private static final int SHADOW_MAP_SIZE = 4096;
    private static final int SHADOW_PCF_COUNT = 1;
    private static float AMBIENT_LIGHT = 0.05f;
    private static float SKYBOX_BLEND = 0.7f;                                                                          // mix of 2 Engine.modules.skybox  skybox1| 0-2f |skybox2

    public static float getSkyboxBlend() {
        return SKYBOX_BLEND;
    }

    public static void setSkyboxBlend(float skyboxBlend) {
        SKYBOX_BLEND = skyboxBlend;
    }

    public static float getFogDencity() {
        return FOG_DENCITY;
    }

    public static float getFogGradient() {
        return FOG_GRADIENT;
    }

    public static float getSkyboxSize() {
        return SKYBOX_SIZE;
    }

    public static float getFOV() {
        return FOV;
    }

    public static float getNearPlane() {
        return NEAR_PLANE;
    }

    public static float getFarPlane() {
        return FAR_PLANE;
    }

    public static Vector3f getEnvRgb() {
        return ENV_RGB;
    }

    public static float getAmbientLight() {
        return AMBIENT_LIGHT;
    }

    public static void setAmbientLight(float ambientLight) {
        AMBIENT_LIGHT = ambientLight;
    }

    public static float getMousePickerRootObjectRange() {
        return MOUSE_PICKER_ROOT_OBJECT_RANGE;
    }

    public static float getLodBias() {
        return LOD_BIAS;
    }

    public static int getMaxLights() {
        return MAX_LIGHTS;
    }

    public static float getSkyboxRotateSpeed() {
        return SKYBOX_ROTATE_SPEED;
    }

    public static float getShadowOffset() {
        return SHADOW_OFFSET;
    }

    public static Vector4f getShadowUp() {
        return SHADOW_UP;
    }

    public static Vector4f getShadowForward() {
        return SHADOW_FORWARD;
    }

    public static float getShadowDistance() {
        return SHADOW_DISTANCE;
    }

    public static int getShadowMapSize() {
        return SHADOW_MAP_SIZE;
    }

    public static int getShadowPcfCount() {
        return SHADOW_PCF_COUNT;
    }

    public static String getResPath() {
        return RES_PATH;
    }

    public static int getWindowWidth() {
        return WINDOW_WIDTH;
    }

    public static int getWindowHeight() {
        return WINDOW_HEIGHT;
    }

    public static String getGameTitle() {
        return GAME_TITLE;
    }

    public static int getFpsCap() {
        return FPS_CAP;
    }
}
