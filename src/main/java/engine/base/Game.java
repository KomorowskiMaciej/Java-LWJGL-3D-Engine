package engine.base;

import engine.modules.gameObject.GameObject;
import engine.modules.gameObject.gameObjectComponents.CameraBaseComponent;
import engine.modules.gameObject.gameObjectComponents.TerrainRendererComponent;
import engine.modules.guis.GuiTexture;
import engine.modules.light.Light;
import engine.modules.water.WaterTile;

import java.util.ArrayList;
import java.util.List;


public abstract class Game {

    private static CameraBaseComponent camera;
    private static List<Light> lights = new ArrayList<Light>();
    private static List<WaterTile> waterTiles = new ArrayList<WaterTile>();
    private static List<GuiTexture> guiTextures = new ArrayList<>();
    private static TerrainRendererComponent terrain;
    protected List<GameObject> gameObjects = new ArrayList<GameObject>();

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

    public List<WaterTile> getWaters() {
        return waterTiles;
    }

    public List<GuiTexture> getGuiTextures() {
        return guiTextures;
    }

    public void setGuiTexture(GuiTexture texture) {
        guiTextures.add(texture);
    }

    protected void setLight(Light light) {
        lights.add(light);
    }

    protected void setWaterTile(WaterTile water) {
        waterTiles.add(water);
    }
}
