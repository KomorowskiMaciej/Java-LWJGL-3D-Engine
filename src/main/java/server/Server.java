package server;

import com.google.gson.Gson;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.net.*;
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
    private ConcurrentLinkedQueue<GamePackage> incomingInfoQueue = new ConcurrentLinkedQueue<>();
    private ScheduledExecutorService broadcastingExecutor = Executors.newScheduledThreadPool(1);
    private DatagramSocket datagramSocket;
    private Gson gson;

    Server(Gson gson) throws SocketException {
        datagramSocket = new DatagramSocket();
        this.gson = gson;
    }

    void listenOnTcp() throws IOException {
        int port = DEFAULT_TCP_PORT;
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

                        ServerGameObject serverGameObject = new ServerGameObject(newUserID, new Vector3f(400, 0, 400),
                                new Vector3f(0, 0, 0), out, socket);
                        serverGameObjects.put(newUserID, serverGameObject);

                        out.writeObject(
                                new GamePackage(
                                        Constants.OpCode.USER_LOGIN,
                                        serverGameObject.getUserID(),
                                        new Vector3f(400, 0, 400),
                                        new Vector3f(0, 0, 0),
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
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }


    void startBroadcastingTcp() {
        broadcastingExecutor.scheduleAtFixedRate(() -> {
            updateIncomingGameObjects();
            serverGameObjects.forEach((k, v) -> serverGameObjects.forEach((a, b) -> {
                try {
                    v.getOut().writeObject(new GamePackage(Constants.OpCode.USER_STATE, b.getUserID(), b.getPosition(), b.getRotation(), 100));
                    v.getOut().flush();
                } catch (IOException e) {
                    logger.info(String.format("Player %s, has been disconnected.", v.getUserID()));
                    String id = v.getUserID();
                    serverGameObjects.remove(id);
                    serverGameObjects.forEach((k2, v2) -> {
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

    void startBroadcastingUdp() throws IOException {

        broadcastingExecutor.scheduleAtFixedRate(() -> {
            updateIncomingGameObjects();
            serverGameObjects.forEach((k, v) -> serverGameObjects.forEach((a, serverGameObject) -> {
                try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(DATAGRAM_PACKET_SIZE);
                     ObjectOutputStream objectOut = new ObjectOutputStream(new BufferedOutputStream(byteOut))) {

                    GamePackage gamePackage = new GamePackage(Constants.OpCode.USER_STATE
                            , serverGameObject.getUserID(), serverGameObject.getPosition()
                            , serverGameObject.getRotation(), 100);
                    objectOut.flush();
                    objectOut.writeObject(gamePackage);
                    objectOut.flush();
                    byte[] gamePackageBytes = byteOut.toByteArray();
                    DatagramPacket datagramPacket = new DatagramPacket(gamePackageBytes, gamePackageBytes.length
                            , MULTICAST_GROUP_ADDRESS, DEFAULT_UDP_PORT);

                    datagramSocket.send(datagramPacket);
                    logger.info(String.format("Sent datagram of Player ID %s", v.getUserID()));
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.info(String.format("Failed to send Player %s datagram", v.getUserID()));
                }
            }));
        }, 0, SERVER_BROADCAST_RATE_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    private void updateIncomingGameObjects() {
        while (incomingInfoQueue.size() > 0) {
            GamePackage gamePackage = incomingInfoQueue.poll();
            if (gamePackage.getOpCode() != Constants.OpCode.USER_STATE)
                throw new IllegalStateException("Package in queue is not valid - wrong opcode.");

            if (!serverGameObjects.containsKey(gamePackage.getUserID()))
                throw new IllegalStateException("Package in queue in not valid - missing object with this user id in hash map.");

            ServerGameObject serverGameObject = serverGameObjects.get(gamePackage.getUserID());

            if (!serverGameObject.getUserID().equals(gamePackage.getUserID()))
                throw new IllegalStateException("Package in queue in not equal to object in hashmap.");
            if (gamePackage.getPosition() == null || gamePackage.getRotation() == null)
                throw new IllegalStateException("Package contains null values!");


            serverGameObject.setPosition(gamePackage.getPosition());
            serverGameObject.setRotation(gamePackage.getRotation());
            serverGameObjects.put(serverGameObject.getUserID(), serverGameObject);
        }
    }

}
