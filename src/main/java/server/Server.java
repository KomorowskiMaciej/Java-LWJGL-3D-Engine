package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static server.Constants.SERVER_BROADCAST_RATE_MILLISECOND;

/**
 * Created by stasbar on 23.03.2017.
 */

@SuppressWarnings("InfiniteLoopStatement")
class Server {

    private ConcurrentHashMap<String, UserServerState> userServerStates = new ConcurrentHashMap<>();
    private ScheduledExecutorService broadcastingExecutor = Executors.newScheduledThreadPool(1);

    void listenOn(int port) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(String.format("Listening on port %d", port));

            while (!Thread.interrupted()) {

                Socket socket = serverSocket.accept();
                new Thread(() -> process(socket)).start();
            }
        }
    }

    private void process(Socket socket) {

        System.out.println("Process socket from: " + socket.getInetAddress().getHostAddress());

        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            while (!Thread.interrupted()) {
                int opCode = in.readInt();
                System.out.println(String.format("Received opCode %d", opCode));
                switch (opCode) {
                    case Constants.OpCode.LOGIN: {
                        String newUserID = UUID.randomUUID().toString();
                        System.out.println(String.format("Login new user with userID %s", newUserID));
                        UserServerState userServerState = new UserServerState(newUserID, out);
                        userServerStates.put(newUserID, userServerState);

                        out.writeInt(Constants.OpCode.LOGIN);
                        out.flush();
                        System.out.println("Wrote int");
                        out.writeObject(userServerState.getUserState());
                        out.flush();
                        System.out.println(String.format("Wrote login %s", newUserID));

                    }
                    break;
                    case Constants.OpCode.USER_STATE: {

                        try {
                            UserState userState = (UserState) in.readObject();
                            userState.print();
                            UserServerState userServerState = userServerStates.get(userState.getUserID());
                            System.out.println(String.format("Update %s %s", userState.getUserID(), userState.toString()));
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
            for (Object entry : userServerStates.entrySet()) {
                Map.Entry pair = (Map.Entry) entry;
                UserServerState userServerState = (UserServerState) pair.getValue();
                userServerState.sendGameState(userServerStates.values());
            }
        }, 0, SERVER_BROADCAST_RATE_MILLISECOND, TimeUnit.MILLISECONDS);
    }


}
