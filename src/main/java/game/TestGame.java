package game;

import engine.base.Game;
import engine.base.resourceManagment.containers.animation.AnimatedModel;
import engine.base.resourceManagment.containers.animation.Animation;
import engine.base.resourceManagment.animationLoader.AnimatedModelLoader;
import engine.base.resourceManagment.animationLoader.AnimationLoader;
import engine.base.gameObject.GameObject;
import engine.base.gameObject.gameObjectComponents.*;
import engine.modules.light.Light;
import engine.base.resourceManagment.Loader;
import engine.base.resourceManagment.objParser.OBJLoader;
import engine.base.resourceManagment.containers.model.Model;
import engine.base.resourceManagment.containers.texture.TerrainTexture;
import engine.base.resourceManagment.containers.texture.TerrainTexturePack;
import engine.base.resourceManagment.containers.texture.Texture;
import engine.modules.network.*;
import engine.base.resourceManagment.containers.file.ResourceFile;
import org.lwjgl.util.vector.Vector3f;
import server.Constants;
import server.GamePackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Maciek on 12.07.2016.
 */
public class TestGame extends Game {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    public ConcurrentLinkedQueue<NetworkEvent> queue = new ConcurrentLinkedQueue<>();
    private HashMap<String, GameObject> players = new HashMap<>();

    private Model playerModel = null;
    private AnimatedModel animatedPlayerModel = null;
    private Animation runAnimation;
    private Animation idleAnimation;

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
        setUpFirstPersonCamera(player);
        // MULTIPLAYER
        setUpMultiplayer();

    //    OpenGlUtils.goWireframe(true)

//        GameObject testObject = new GameObject(new Vector3f(405,-10, 400),new Vector3f(0,0,0),new Vector3f(5,5, 5));
//        testObject.AddComponent(new ModelComponent(playerModel));
//        gameObjects.add(testObject);
//
//        GameObject test2Object = new GameObject(new Vector3f(400,-10, 400),new Vector3f(0,0,0),new Vector3f(1,1, 1));
//        test2Object.AddComponent(new AnimatedModelComponent(animatedPlayerModel));
//        gameObjects.add(test2Object);
//        animatedPlayerModel.doAnimation(runAnimation);
    }

    public void update() {
        if (isMultiplayer)
            updateMultiplayer();
    }

    private void updateMultiplayer() {

        // WYKONYWANIE POLECEN SERWERA
        while (queue.size() > 0) {
            NetworkEvent event = queue.poll();

            switch (event.Type) {
                case Constants.OpCode.USER_STATE: {
                    GamePackage state = (GamePackage) event.Data;
                    if(!state.getUserID().equals(playerUserID) && state.getPosition() != null){

                        if (players.containsKey(state.getUserID())) {
                            GameObject go = players.get(state.getUserID());
                            if(go.getPosition().equals(state.getPosition())){
                                // todo: do poprawienia
                                AnimatedModelComponent component = go.getComponent(AnimatedModelComponent.class);
                                component.getModel().doAnimation(idleAnimation);
                            } else {
                                AnimatedModelComponent component = go.getComponent(AnimatedModelComponent.class);
                                component.getModel().doAnimation(runAnimation);
                            }

                            go.setPosition(new Vector3f(state.getPosition().x, state.getPosition().y, state.getPosition().z));
                            go.setRotation(new Vector3f(state.getRotation().x, state.getRotation().y, state.getRotation().z));
                        } else {
                            players.put(state.getUserID(),createExternalPlayer(
                                    new Vector3f(state.getPosition().x, state.getPosition().y, state.getPosition().z),
                                    new Vector3f(state.getRotation().x, state.getRotation().y, state.getRotation().z)
                                    )
                            );
                        }
                    }
                    break;
                }
                case Constants.OpCode.USER_LOGOUT: {
                    GamePackage state = (GamePackage) event.Data;
                    if(!state.getUserID().equals(playerUserID)){
                       deleteExternalPlayer(playerUserID);
                    }
                    break;
                }
            }
        }

        // WYSYLANIE INFO DO SERWERA
        PlayerComponent baseComponent = player.getComponent(PlayerComponent.class);
        if (baseComponent == null)
            throw new IllegalStateException("Player base component == null during sending data to server.");
        try {
            GamePackage playerState = new GamePackage(Constants.OpCode.USER_STATE, playerUserID, new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z), player.getRotation(), baseComponent.getHp());
            out.writeObject(playerState);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameObject createExternalPlayer(Vector3f position, Vector3f rotation) {
        GameObject p = new GameObject(position, rotation, new Vector3f(1, 1, 1));
        p.AddComponent(new AnimatedModelComponent(animatedPlayerModel));
        animatedPlayerModel.doAnimation(idleAnimation);
        gameObjects.add(p);
        return p;
    }

    public void deleteExternalPlayer(String UserID){
        if(players.containsKey(UserID)){
            gameObjects.remove(players.get(UserID));
            players.remove(UserID);
        }
    }

private String playerUserID = null;
    public void setPlayerStartStates(GamePackage state) {

        if (state.getPosition() == null || state.getRotation() == null)
            throw new IllegalStateException("GamePackage can't passes nulls.");
        player.setPosition(state.getPosition());
        player.setRotation(state.getRotation());
        playerUserID = state.getUserID();

       PlayerComponent c = player.getComponent(PlayerComponent.class);
       if (c == null)
            throw new IllegalStateException("FirstPersonMovementComponent hasnt found in player object");
        c.setHp(state.getHp());
    }

    private void setUpMultiplayer() {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 1234);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            login(out, in); // synchronous process -> block thread until successfully log in
            executorService.submit(new ReceiverThread(in, queue)); // Start listening for server's userStates broadcasting
            isMultiplayer = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPlayer(Vector3f position, Vector3f rotation, boolean renderMesh) {
        player = new GameObject(position, rotation, new Vector3f(5, 5, 5));
        PhysicsComponent physicsComponent = new PhysicsComponent();
        player.AddComponent(physicsComponent);
        if (renderMesh)
            player.AddComponent(new ModelComponent(playerModel, 0));
        gameObjects.add(player);
        player.AddComponent(new FirstPersonMovementComponent(physicsComponent));
        player.AddComponent(new PlayerComponent());
    }

    private void login(ObjectOutputStream out, ObjectInputStream in) throws IOException {
        out.writeObject(new GamePackage(Constants.OpCode.USER_LOGIN));
        out.flush();

        try {
            GamePackage returnLoginPackage = (GamePackage) in.readObject();

            if(returnLoginPackage.getOpCode() != Constants.OpCode.USER_LOGIN)
                throw new FailedLoginException(String.format("Received wrong opcode [%d] during login process", returnLoginPackage.getOpCode()));

            if(returnLoginPackage.getRotation() == null || returnLoginPackage.getRotation() == null)
                throw new FailedLoginException(String.format("Login return package have null required fields!"));

            setPlayerStartStates(returnLoginPackage);
            System.out.println("Loggned in");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void setUpModelsAndTextures() {
        playerModel = new Model(OBJLoader.loadOBJ("lumberJack"), new Texture(Loader.getInstance().loadTexture("lumberJack_diffuse"))).setDisableCulling(false);
        animatedPlayerModel = AnimatedModelLoader.loadEntity(new ResourceFile("res/dae/model2.dae"),  new ResourceFile("res/textures/animatedModel.png"));
        runAnimation = AnimationLoader.loadAnimation(new ResourceFile("res/dae/model2.dae"));
        idleAnimation = AnimationLoader.loadAnimation(new ResourceFile("res/dae/idleAnimation.dae"));
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
        setLight(new Light(new Vector3f(1000000, 10000000, 1000000), new Vector3f(1f, 1f, 1f)));
      //  setLight(new Light(new Vector3f(400, -4.7f, 400), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
    }
}
