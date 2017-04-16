package engine.base;

import engine.base.resourceManagment.containers.animation.AnimatedModel;
import engine.base.gameObject.GameObject;
import engine.base.gameObject.gameObjectComponents.CameraBaseComponent;
import engine.base.gameObject.gameObjectComponents.FirstPersonCamera;
import engine.base.gameObject.gameObjectComponents.TerrainRendererComponent;
import engine.base.gameObject.gameObjectComponents.ThirdPersonCamera;
import engine.modules.guis.GuiTexture;
import engine.modules.light.Light;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;


public abstract class Game {

    private static CameraBaseComponent camera;
    private static List<Light> lights = new ArrayList<Light>();
    private static List<GuiTexture> guiTextures = new ArrayList<>();
    private static List<AnimatedModel> animatedModels = new ArrayList<>();
    private static TerrainRendererComponent terrain;
    protected List<GameObject> gameObjects = new ArrayList<>();

    public static TerrainRendererComponent getTerrain() {
        return terrain;
    }

    protected void setTerrain(TerrainRendererComponent terrain) {
        this.terrain = terrain;
    }

    public static CameraBaseComponent getCamera() {
        return camera;
    }

    protected void setCamera(CameraBaseComponent camera) {
        this.camera = camera;
    }

    public static Light getSun() {
        return lights.get(0);
    }

    public static TerrainRendererComponent GetTerrain() {
        return terrain;
    }

    public List<GameObject> getGameObjectList() {
        return gameObjects;
    }

    public void init() {
    }

    public void update() {
    }

    public void input() {
    }

    public void cleanUp() {
    }

    public List<Light> getLights() {
        return lights;
    }


    public List<GuiTexture> getGuiTextures() {
        return guiTextures;
    }

    public void setGuiTexture(GuiTexture texture) {
        guiTextures.add(texture);
    }
    public void setAnimatedModel(AnimatedModel animatedModel) {
        animatedModels.add(animatedModel);
    }

    protected void setLight(Light light) {
        lights.add(light);
    }

    // CAMERA

    public void setUpFirstPersonCamera(GameObject player) {
        GameObject cameraObj = new GameObject(new Vector3f(0, 0, 0), new Vector3f(20, 0, 0), new Vector3f(1, 1, 1));
        setCamera(new FirstPersonCamera(player));
        cameraObj.AddComponent(getCamera());
        gameObjects.add(cameraObj);
    }


    public void setUpThirdPersonCamera(GameObject player) {
        GameObject cameraObj = new GameObject(new Vector3f(0, 0, 0), new Vector3f(20, 0, 0), new Vector3f(1, 1, 1));
        setCamera(new ThirdPersonCamera(player));
        cameraObj.AddComponent(getCamera());
        gameObjects.add(cameraObj);
    }

    public List<AnimatedModel> getAnimatedModels() {
        return animatedModels;
    }
}
