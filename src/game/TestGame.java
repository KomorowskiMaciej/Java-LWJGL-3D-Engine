package game;

import engine.base.Game;
import engine.modules.gameObject.GameObject;
import engine.modules.gameObject.gameObjectComponents.*;
import engine.modules.input.Input;
import engine.modules.light.Light;
import engine.modules.resourceMenegment.Loader;
import engine.modules.resourceMenegment.OBJLoader;
import engine.modules.resourceMenegment.containers.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

/**
 * Created by Maciek on 12.07.2016.
 */
public class TestGame extends Game {

    GameObject targetObject = null;

    public void init() {

        /*
        *
        * terrain generation
        *
        * */


        TerrainTexture backTerrainTexture = new TerrainTexture(Loader.getInstance().loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(Loader.getInstance().loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(Loader.getInstance().loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(Loader.getInstance().loadTexture("path"));
        TerrainTexture blendMap = new TerrainTexture(Loader.getInstance().loadTexture("blendMap"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backTerrainTexture, rTexture, gTexture, bTexture);

        GameObject terrainObj = new GameObject();
        TerrainRendererComponent terrain = new TerrainRendererComponent(terrainObj, 0, 0, texturePack, blendMap);
        terrainObj.AddComponent(terrain);
        setTerrain(terrain);
        gameObjects.add(terrainObj);


        /*
        *
        * object generation
        *
        * */


        Random random = new Random();
        Mesh tmpMesh = OBJLoader.loadOBJ("tree");
        Model tmp = new Model(tmpMesh, new Texture(Loader.getInstance().loadTexture("tree")));

        for (int i = 0; i < 500; i++) {
            Vector3f position = new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * 800);
            position.y = GetTerrain().getHeightOfTerrain(position.x, position.z);
            float randomSize = random.nextFloat() * 4 + 2;
            gameObjects.add(new GameObject(position, new Vector3f(0, 0, 0), new Vector3f(randomSize, randomSize, randomSize)).AddComponent(new MeshRendererComponent(tmp, 0)));
        }


        tmpMesh = OBJLoader.loadOBJ("lowPolyTree");
        tmp = new Model(tmpMesh, new Texture(Loader.getInstance().loadTexture("lowPolyTree")));

        for (int i = 0; i < 500; i++) {
            Vector3f position = new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * 800);
            position.y = GetTerrain().getHeightOfTerrain(position.x, position.z);
            float randomSize = random.nextFloat() * 1 + 1;
            gameObjects.add(new GameObject(position, new Vector3f(0, 0, 0), new Vector3f(randomSize, randomSize, randomSize)).AddComponent(new MeshRendererComponent(tmp, 0)));
        }

        tmpMesh = OBJLoader.loadOBJ("fern");
        tmp = new Model(tmpMesh, new Texture(Loader.getInstance().loadTexture("fern")).setHasTransparency(true), 2);

        for (int i = 0; i < 500; i++) {
            Vector3f position = new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * 800);
            position.y = GetTerrain().getHeightOfTerrain(position.x, position.z);
            gameObjects.add(new GameObject(position, new Vector3f(0, 0, 0)).AddComponent(new MeshRendererComponent(tmp, 1)));
        }
        for (int i = 0; i < 500; i++) {
            Vector3f position = new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * 800);
            position.y = GetTerrain().getHeightOfTerrain(position.x, position.z);
            gameObjects.add(new GameObject(position, new Vector3f(0, 0, 0)).AddComponent(new MeshRendererComponent(tmp, 3)));
        }
        for (int i = 0; i < 500; i++) {
            Vector3f position = new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * 800);
            position.y = GetTerrain().getHeightOfTerrain(position.x, position.z);
            gameObjects.add(new GameObject(position, new Vector3f(0, 0, 0)).AddComponent(new MeshRendererComponent(tmp, 4)));
        }

        tmpMesh = OBJLoader.loadOBJ("boulder");
        tmp = new Model(tmpMesh, new Texture(Loader.getInstance().loadTexture("boulder")), 1);
        for (int i = 0; i < 100; i++) {
            Vector3f position = new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * 800);
            position.y = GetTerrain().getHeightOfTerrain(position.x, position.z);
            gameObjects.add(new GameObject(position,
                    new Vector3f(random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360),
                    new Vector3f(random.nextFloat() * 0.5f + 0.5f, random.nextFloat() * 0.5f + 0.5f, random.nextFloat() * 0.5f + 0.5f)
            ).AddComponent(new MeshRendererComponent(tmp, 0)));
        }


        Model playerModel = new Model(OBJLoader.loadOBJ("lumberJack"), new Texture(Loader.getInstance().loadTexture("lumberJack_diffuse"))).setDisableCulling(true);

        GameObject player = new GameObject(new Vector3f(400, 0, 400), new Vector3f(0, 0, 0), new Vector3f(5, 5, 5));
        player.AddComponent(new MeshRendererComponent(playerModel, 0));
        PhysicsComponent playerphysics = new PhysicsComponent();
        player.AddComponent(playerphysics);
        player.AddComponent(new PlayerBaseComponent(playerphysics));
        gameObjects.add(player);


        //sun
        setLight(new Light(new Vector3f(1000000, 10000000, 1000000), new Vector3f(1f, 1f, 1f)));
        //setWaterTile(new WaterTile(400,400,-10));

        //lamplights
//        setLight(new Light(new Vector3f(400,-4.7f,400),new Vector3f(2,0,0),new Vector3f(1,0.01f,0.002f)));
//        setLight(new Light(new Vector3f(400,4.2f,-400),new Vector3f(0,2,2),new Vector3f(1,0.01f,0.002f)));
//        setLight(new Light(new Vector3f(400,-6.8f,-400),new Vector3f(2,2,0),new Vector3f(1,0.01f,0.002f)));


        GameObject cameraObj = new GameObject(new Vector3f(0, 0, 0), new Vector3f(20, 0, 0), new Vector3f(1, 1, 1));
        setCamera(new ThirdPersonCamera(player));
        cameraObj.AddComponent(getCamera());
        gameObjects.add(cameraObj);

    }

    public void update() {
        if (Mouse.isButtonDown(0)) {
            if (targetObject == null) {
                targetObject = Input.getTargetObject();
            }
            if (targetObject != null)
                targetObject.setPosition(Input.getCurrentTerrainPoint());
        } else
            targetObject = null;
    }
}
