package server;

import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
                    switch (gamePackage.getOpCode()) {
                        case Constants.OpCode.USER_LOGIN: {
                            String newUserID = UUID.randomUUID().toString();
                            logger.info(String.format("New login request, created user id: %s", newUserID));

                            ServerGameObject serverGameObject = new ServerGameObject(newUserID,new Vector3f(400, 0,400),
                                    new Vector3f(0, 0,0), out, socket);
                            serverGameObjects.put(newUserID, serverGameObject);

                            out.writeObject(
                                    new GamePackage(
                                            Constants.OpCode.USER_LOGIN,
                                            serverGameObject.getUserID(),
                                            new Vector3f(400, 0,400),
                                            new Vector3f(0, 0,0),
                                            100
                                    ));
                            out.flush();
                        }
                        break;
                        case Constants.OpCode.USER_STATE: {
                            incomingInfoQueue.add(gamePackage);
                        }
                        break;
                    }
            }
        } catch (ClassNotFoundException | IOException e){

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

            serverGameObjects.forEach((k,v)-> serverGameObjects.forEach((a,b)->{
                        try {
                            v.getOut().writeObject(new GamePackage(Constants.OpCode.USER_STATE, b.getUserID(), b.getPosition(), b.getRotation(), 100));
                            v.getOut().flush();
                        }catch (IOException e) {
                            logger.info(String.format("Player %s, has been disconnected.", v.getUserID()));
                            String id = v.getUserID();
                            serverGameObjects.remove(id);
                            serverGameObjects.forEach((k2,v2)->{
                                try {
                                    v2.getOut().writeObject(new GamePackage(Constants.OpCode.USER_LOGOUT, id));
                                    v2.getOut().flush();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            });
                        }
                    }));
        }, 0, SERVER_BROADCAST_RATE_MILLISECOND, TimeUnit.MILLISECONDS);
    }
}
