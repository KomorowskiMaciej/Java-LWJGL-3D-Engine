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
import engine.network.ReceiverThread;
import engine.network.Sender;
import org.lwjgl.util.vector.Vector3f;
import server.Constants;
import server.UserState;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Maciek on 12.07.2016.
 */
public class TestGame extends Game {

    private HashMap<String, GameObject> players = new HashMap<>();
    Model playerModel = null;
    private GameObject player = null;


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
        if(isMultiplayer)
            updateMultiplayer();
    }


    private void updateMultiplayer(){


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
        UserState userState = new UserState();

        userState.setPosition(player.getPosition());
        userState.setRotation(player.getRotation());

        PlayerBaseComponent baseComponent = player.getComponent(PlayerBaseComponent.class);
        if(baseComponent == null)
            throw new IllegalStateException("Player base component == null during sending data to server.");
        userState.setHp(baseComponent.getHp());

        Sender.send(NetworkEvent.PLAYER_MOVE, userState);
    }


    public GameObject createExternalPlayer(Vector3f position, Vector3f rotation) {
        GameObject player = new GameObject(position, rotation, new Vector3f(5, 5, 5));
        player.AddComponent(new MeshRendererComponent(playerModel, 0));
        gameObjects.add(player);
        return player;
    }


    public void setPlayerStartStates(UserState state){

        if(state.getPosition() == null || state.getRotation() == null)
            throw new IllegalStateException("UserState can't passes nulls.");

        player.setPosition(state.getPosition());
        player.setRotation(state.getRotation());

        PlayerBaseComponent c = player.getComponent(PlayerBaseComponent.class);
        if(c == null)
            throw new IllegalStateException("PlayerBaseComponent hasnt found in player object");
        c.setHp(state.getHp());
    }


    public void updateExternalPlayer(UserState state) {

        // walidacja

        if(state.getPosition() == null || state.getRotation() == null)
            throw new IllegalStateException("UserState can't passes nulls.");

        GameObject p = players.get(state.getUserID());
            if(p == null)
                throw new IllegalStateException("State.UserId doesnt match to any player.");

        p.setPosition(state.getPosition());
        PlayerBaseComponent c = p.getComponent(PlayerBaseComponent.class);
        assert c != null;
        c.setHp(state.getHp());
    }

    public void deleteExternalPlayer(GameObject player) {
        gameObjects.remove(player);
    }



    private void setUpMultiplayer(){
        try {
            Socket socket = new Socket("localhost", 1234);
            if (socket.isConnected()) {
                new Thread(new ReceiverThread(socket)).start();

                Sender.init(socket);
                Sender.send(Constants.OpCode.LOGIN);
                isMultiplayer = true;
            }
        } catch (IOException e) {
            System.out.println("Cannot connect to the server - offline mode.");
        }

    }

    private void setUpModelsAndTextures(){
        playerModel = new Model(OBJLoader.loadOBJ("lumberJack"), new Texture(Loader.getInstance().loadTexture("lumberJack_diffuse"))).setDisableCulling(true);
    }
    private void setUpTerrain (){

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
    private void setUpLights(){
        setLight(new Light(new Vector3f(1000000, 10000000, 1000000), new Vector3f(0.4f, 0.2f, 0.3f)));
        setLight(new Light(new Vector3f(400, -4.7f, 400), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
    }
    private void setUpCamera(){
        GameObject cameraObj = new GameObject(new Vector3f(0, 0, 0), new Vector3f(20, 0, 0), new Vector3f(1, 1, 1));
        setCamera(new FirstPersonCamera(player));
        cameraObj.AddComponent(getCamera());
        gameObjects.add(cameraObj);
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
}
