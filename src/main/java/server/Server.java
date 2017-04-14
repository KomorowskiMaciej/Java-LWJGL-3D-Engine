package server;

import engine.network.NetworkEvent;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static server.Constants.*;

/**
 * Created by stasbar on 23.03.2017.
 */

@SuppressWarnings("InfiniteLoopStatement")
class Server {

    private Logger logger = Logger.getLogger(Server.class.getCanonicalName());
    private ConcurrentHashMap<String, ServerGameObject> serverGameObjects = new ConcurrentHashMap<>();
    public ConcurrentLinkedQueue<GamePackage> incomingInfoQueue = new ConcurrentLinkedQueue<>();
    private ScheduledExecutorService broadcastingExecutor = Executors.newScheduledThreadPool(1);

    void listenOn(int port) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info(String.format("Listening on port %d", port));

            while (!Thread.interrupted()) {

                Socket socket = serverSocket.accept();
                new Thread(() -> process(socket)).start();
            }
        }
    }

    private void process(Socket socket) {

        logger.info("Process socket from: " + socket.getInetAddress().getHostAddress());

        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            while (!Thread.interrupted()) {

                    GamePackage gamePackage = (GamePackage) in.readObject();
                    logger.info(String.format("Received opCode %d", gamePackage.getOpCode()));

                    switch (gamePackage.getOpCode()) {
                        case Constants.OpCode.LOGIN: {
                            String newUserID = UUID.randomUUID().toString();
                            logger.info(String.format("Recieved login request, created user id: %s", newUserID));

                            ServerGameObject serverGameObject = new ServerGameObject(newUserID,new Vector3f(400, 0,400),
                                    new Vector3f(0, 0,0), out);
                            serverGameObjects.put(newUserID, serverGameObject);

                            out.writeObject(
                                    new GamePackage(
                                            Constants.OpCode.LOGIN,
                                            serverGameObject.getUserID(),
                                            new Vector3f(400, 0,400),
                                            new Vector3f(0, 0,0),
                                            100
                                    ));
                            out.flush();
                            logger.info(String.format("Login package has been sent, user id: %s", newUserID));
                        }
                        break;
                        case Constants.OpCode.USER_STATE: {
                            System.out.println("Recieved user state package. Queue size = "+ incomingInfoQueue.size() +
                                    " Position: " + gamePackage.getPosition() +
                                    " Rotation: " + gamePackage.getRotation()
                            );
                            incomingInfoQueue.add(gamePackage);
                        }
                        break;
                    }
            }
        } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
        }
    }


    void startBroadcasting() {
        broadcastingExecutor.scheduleAtFixedRate(() -> {

            while (incomingInfoQueue.size() > 0) {

                GamePackage gamePackage = incomingInfoQueue.poll();

                if(gamePackage.getOpCode() != Constants.OpCode.USER_STATE)
                    throw new IllegalStateException("Package in queue is not valid - wrong opcode.");

                if(!serverGameObjects.containsKey(gamePackage.getUserID()))
                    throw new IllegalStateException("Package in queue in not valid - missing object with this user id in hash map.");

                ServerGameObject serverGameObject = serverGameObjects.get(gamePackage.getUserID());

                if(!serverGameObject.getUserID().equals(gamePackage.getUserID()))
                    throw new IllegalStateException("Package in queue in not equal to object in hashmap.");
                if(gamePackage.getPosition() == null || gamePackage.getRotation() == null)
                    throw new IllegalStateException("Package contains null values!");


                serverGameObject.setPosition(gamePackage.getPosition());
                serverGameObject.setRotation(gamePackage.getRotation());
                serverGameObjects.replace(serverGameObject.getUserID(), serverGameObject);
            }

            serverGameObjects.forEach((k,v)->{
                    serverGameObjects.forEach((a,b)->{
                        try {
                            if(!v.getUserID().equals(b.getUserID())){
                                v.getOut().writeObject(new GamePackage(Constants.OpCode.USER_STATE, b.getUserID(), b.getPosition(), b.getRotation(), 100));
                                v.getOut().flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("broadcast sent");
                    });

            });
        }, 0, SERVER_BROADCAST_RATE_MILLISECOND, TimeUnit.MILLISECONDS);
    }


}
