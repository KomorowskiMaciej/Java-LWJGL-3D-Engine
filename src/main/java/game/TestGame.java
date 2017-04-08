package game;

import engine.base.Game;
import engine.modules.gameObject.GameObject;
import engine.modules.gameObject.gameObjectComponents.*;
import engine.modules.light.Light;
import engine.modules.resourceMenegment.Loader;
import engine.modules.resourceMenegment.OBJLoader;
import engine.modules.resourceMenegment.containers.*;
import engine.network.EventQueue;
import engine.network.NetworkEvent;
import engine.network.UpdatePlayerState;
import org.lwjgl.util.vector.Vector3f;
import server.UserState;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Maciek on 12.07.2016.
 */
public class TestGame extends Game {
    private final ObjectOutputStream objectOutputStream;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    GameObject targetObject = null;
    Model playerModel = null;
    private HashMap<String, GameObject> players = new HashMap<>();
    private UserState userState;
    private GameObject player = null;

    public TestGame(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public void init() {


        /*
            load models and textures
         */

        playerModel = new Model(OBJLoader.loadOBJ("lumberJack"), new Texture(Loader.getInstance().loadTexture("lumberJack_diffuse"))).setDisableCulling(true);

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

        // TERRAIN


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


        setLight(new Light(new Vector3f(1000000, 10000000, 1000000), new Vector3f(0.4f, 0.2f, 0.3f)));
        setLight(new Light(new Vector3f(400, -4.7f, 400), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));


        // PLAYER

        player = createPlayer(new Vector3f(400, 0, 400), new Vector3f(0, 0, 0), false);



        // CAMERA
        GameObject cameraObj = new GameObject(new Vector3f(0, 0, 0), new Vector3f(20, 0, 0), new Vector3f(1, 1, 1));
        setCamera(new FirstPersonCamera(player));
        cameraObj.AddComponent(getCamera());
        gameObjects.add(cameraObj);

    }

    public GameObject createPlayer(Vector3f position, Vector3f rotation, boolean renderMesh) {
        GameObject player = new GameObject(position, rotation, new Vector3f(5, 5, 5));
        PhysicsComponent physicsComponent = new PhysicsComponent();
        player.AddComponent(physicsComponent);
        if (renderMesh)
            player.AddComponent(new MeshRendererComponent(playerModel, 0));
        gameObjects.add(player);

        player.AddComponent(new PlayerBaseComponent(physicsComponent));

        return player;
    }

    public GameObject createMultiPlayer(Vector3f position, Vector3f rotation, String name) {
        GameObject player = new GameObject(position, rotation, new Vector3f(5, 5, 5));
        player.AddComponent(new MeshRendererComponent(playerModel, 0));
        gameObjects.add(player);
        return player;
    }

    public void updateMultiPlayer(GameObject player, Vector3f position, Vector3f rotation) {
        player.setPosition(position);
        player.setRotation(rotation);
    }

    public void deleteMultiPlayer(GameObject player) {
        gameObjects.remove(player);
    }

    public void update() {
        while (EventQueue.queue.size() > 0) {
            NetworkEvent event = EventQueue.queue.poll();

            switch (event.Type) {
                case NetworkEvent.LOGIN: {
                    userState = (UserState) event.Data;

                    player.setPosition(userState.getPosition());
                    break;
                }

                case NetworkEvent.PLAYER_MOVE: {
                    UserState state = (UserState) event.Data;

                    if (players.containsKey(state.getUserID())) {
                        GameObject gameObject = players.get(state.getUserID());
                        gameObject.setPosition(state.getPosition());
                        PlayerBaseComponent playerStateComponent = gameObject.getComponent(PlayerBaseComponent.class);
                        // TODO: 08.04.2017 Tutaj sypie nullem - playerStateComponent == null
                        playerStateComponent.setHp(state.getHp());
                    } else {
                        GameObject p = createMultiPlayer(state.getPosition(), new Vector3f(0, 0, 0), state.getUserID());
                        players.put(state.getUserID(), p);
                    }
                    break;
                }
            }
        }

        if (userState != null) {
            userState.setPosition(player.getPosition());
            executorService.submit(new UpdatePlayerState(objectOutputStream, userState));

        }
    }
}
