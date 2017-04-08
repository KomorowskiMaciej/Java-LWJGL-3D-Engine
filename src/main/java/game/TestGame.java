package game;

import engine.base.Game;
import engine.modules.gameObject.GameObject;
import engine.modules.gameObject.gameObjectComponents.*;
import engine.modules.light.Light;
import engine.modules.resourceMenegment.Loader;
import engine.modules.resourceMenegment.OBJLoader;
import engine.modules.resourceMenegment.containers.Model;
import engine.modules.resourceMenegment.containers.TerrainTexture;
import engine.modules.resourceMenegment.containers.TerrainTexturePack;
import engine.modules.resourceMenegment.containers.Texture;
import engine.network.*;
import org.lwjgl.util.vector.Vector3f;
import server.Constants;
import server.UserState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Maciek on 12.07.2016.
 */
public class TestGame extends Game {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private HashMap<String, GameObject> players = new HashMap<>();
    Model playerModel = null;
    private GameObject player = null;
    private UserState playerState = new UserState();
    private boolean isMultiplayer = false;


    public void init() {
        // LOAD MODELS, TEXTURES, ETC.
        setUpModelsAndTextures();
        // TERRAIN
        setUpTerrain();
        // LIGHTS
        setUpLights();
        // PLAYER
        createPlayer(new Vector3f(400, 0, 400), new Vector3f(0, 0, 0), false);
        // CAMERA
        setUpCamera();
        // MULTIPLAYER
        setUpMultiplayer();
    }

    public void update() {
        if (isMultiplayer)
            updateMultiplayer();
    }


    private void updateMultiplayer() {


        // WYKONYWANIE POLECEN SERWERA
        while (EventQueue.queue.size() > 0) {
            NetworkEvent event = EventQueue.queue.poll();

            switch (event.Type) {
                case NetworkEvent.LOGIN: {
                    setPlayerStartStates((UserState) event.Data);
                    break;
                }
                case NetworkEvent.PLAYER_MOVE: {
                    UserState state = (UserState) event.Data;

                    if (players.containsKey(state.getUserID())) {
                        updateExternalPlayer(state);
                    } else {
                        GameObject p = createExternalPlayer(state.getPosition(), state.getRotation());
                        players.put(state.getUserID(), p);
                    }
                    break;
                }
            }
        }

        // WYSYLANIE INFO DO SERWERA


        playerState.setPosition(player.getPosition());
        playerState.setRotation(player.getRotation());

        PlayerBaseComponent baseComponent = player.getComponent(PlayerBaseComponent.class);
        if (baseComponent == null)
            throw new IllegalStateException("Player base component == null during sending data to server.");
        playerState.setHp(baseComponent.getHp());


        executorService.submit(new UpdatePlayerState(out, playerState));
    }


    public GameObject createExternalPlayer(Vector3f position, Vector3f rotation) {
        GameObject player = new GameObject(position, rotation, new Vector3f(5, 5, 5));
        player.AddComponent(new MeshRendererComponent(playerModel, 0));
        gameObjects.add(player);
        return player;
    }


    public void setPlayerStartStates(UserState state) {

        if (state.getPosition() == null || state.getRotation() == null)
            throw new IllegalStateException("UserState can't passes nulls.");
        playerState = state;
        player.setPosition(state.getPosition());
        player.setRotation(state.getRotation());

        PlayerBaseComponent c = player.getComponent(PlayerBaseComponent.class);
        if (c == null)
            throw new IllegalStateException("PlayerBaseComponent hasnt found in player object");
        c.setHp(state.getHp());
    }


    public void updateExternalPlayer(UserState state) {

        // walidacja
        if (state.getPosition() == null || state.getRotation() == null)
            throw new IllegalStateException("UserState can't passes nulls.");

        GameObject gameObject = players.get(state.getUserID());
        if (gameObject == null)
            throw new IllegalStateException("State.UserId doesnt match to any player.");

        gameObject.setPosition(state.getPosition());
        PlayerBaseComponent playerComponent = gameObject.getComponent(PlayerBaseComponent.class);

        assert playerComponent != null;

        playerComponent.setHp(state.getHp());
    }

    private void setUpMultiplayer() {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 1234);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            login(out, in); // synchronous process -> block thread until successfully log in
            executorService.submit(new ReceiverThread(in)); // Start listening for server's userStates broadcasting
            isMultiplayer = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: 08.04.2017 close streams somewhere albo jebać

    }

    private void createPlayer(Vector3f position, Vector3f rotation, boolean renderMesh) {
        player = new GameObject(position, rotation, new Vector3f(5, 5, 5));
        PhysicsComponent physicsComponent = new PhysicsComponent();
        player.AddComponent(physicsComponent);
        if (renderMesh)
            player.AddComponent(new MeshRendererComponent(playerModel, 0));
        gameObjects.add(player);
        player.AddComponent(new PlayerBaseComponent(physicsComponent));
    }


    // todo: refactor

    private static void login(ObjectOutputStream out, ObjectInputStream in) throws IOException {
        out.writeInt(Constants.OpCode.LOGIN);
        out.flush();

        int opCode = in.readInt();
        if (opCode == Constants.OpCode.LOGIN) {
            try {
                UserState newUserState = (UserState) in.readObject();
                EventQueue.queue.add(new NetworkEvent<>(NetworkEvent.LOGIN, newUserState));
                System.out.println("Loggned in");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else
            throw new FailedLoginException(String.format("Received wrong opcode [%d] during login process", opCode));
    }


    private void setUpModelsAndTextures() {
        playerModel = new Model(OBJLoader.loadOBJ("lumberJack"), new Texture(Loader.getInstance().loadTexture("lumberJack_diffuse"))).setDisableCulling(true);
    }

    private void setUpTerrain() {

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
    }

    private void setUpLights() {
        setLight(new Light(new Vector3f(1000000, 10000000, 1000000), new Vector3f(0.4f, 0.2f, 0.3f)));
        setLight(new Light(new Vector3f(400, -4.7f, 400), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
    }

    private void setUpCamera() {
        GameObject cameraObj = new GameObject(new Vector3f(0, 0, 0), new Vector3f(20, 0, 0), new Vector3f(1, 1, 1));
        setCamera(new FirstPersonCamera(player));
        cameraObj.AddComponent(getCamera());
        gameObjects.add(cameraObj);
    }
}
