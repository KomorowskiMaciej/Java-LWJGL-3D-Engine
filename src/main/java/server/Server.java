package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static server.Constants.SERVER_BROADCAST_RATE_MILLISECOND;

/**
 * Created by stasbar on 23.03.2017.
 */

@SuppressWarnings("InfiniteLoopStatement")
class Server {

    private Logger logger = Logger.getLogger(Server.class.getCanonicalName());
    private ConcurrentHashMap<String, UserServerState> userServerStates = new ConcurrentHashMap<>();
    private ScheduledExecutorService broadcastingExecutor = Executors.newScheduledThreadPool(1);

    void listenOn(int port) throws IOException {

        try (ServerSocket serverSocker = new ServerSocket(port)) {
            logger.info(String.format("Listening on port %d", port));

            while (true) {

                Socket socket = serverSocker.accept();
                new Thread(() -> process(socket)).start();
            }
        }
    }

    private void process(Socket socket) {

        logger.info("Process socket from: " + socket.getInetAddress().getHostAddress());

        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            for (; ; ) {
                int opCode = in.readInt();
                logger.info(String.format("Received opCode %d", opCode));
                switch (opCode) {
                    case Constants.OpCode.LOGIN: {
                        String newUserID = UUID.randomUUID().toString();
                        logger.info(String.format("Login new user with userID %ss", newUserID));
                        UserServerState userServerState = new UserServerState(newUserID, socket);
                        userServerStates.put(newUserID, userServerState);

                        out.writeInt(Constants.OpCode.LOGIN);
                        logger.info("Wrote int");
                        out.writeObject(userServerState.getUserState());
                        out.flush();
                        logger.info(String.format("Wrote login %s", newUserID));

                    }
                    break;
                    case Constants.OpCode.USER_STATE: {

                        try {
                            UserState userState = (UserState) in.readObject();
                            UserServerState userServerState = userServerStates.get(userState.getUserID());
                            logger.info(String.format("Update %s %s", userState.getUserID(), userState.toString()));
                            userServerState.getExecutor().submit(new UpdateUserState(userServerState, userState)); //Execute update userServerState method in user's executor
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    void startBroadcasting() {
        broadcastingExecutor.scheduleAtFixedRate(() -> {
            Iterator iterator = userServerStates.entrySet().iterator();
            logger.info("Broadcasting GameState");
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                UserServerState userServerState = (UserServerState) pair.getValue();
                userServerState.sendGameState(userServerStates.values());

            }
        }, 0, SERVER_BROADCAST_RATE_MILLISECOND, TimeUnit.MILLISECONDS);
    }


}
