package server;

import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static server.Constants.DEFAULT_HP;
import static server.Utils.getRandomSprawnVector;


/**
 * Created by stasbar on 24.03.2017.
 */
public class UserServerState {
    private UserState userState;
    private Socket socket;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    UserServerState(String userID, Socket socket) {
        this.userState = new UserState(userID, getRandomSprawnVector(), new Vector3f(0,0,0), DEFAULT_HP);
        this.socket = socket;
    }

    void updateUserState(UserState userState) {
        this.userState.setPosition(userState.getPosition());
    }

    ExecutorService getExecutor() {
        return executorService;
    }

    void sendGameState(Collection<UserServerState> userStateList) {

        userStateList.forEach(userServerState -> {
            try {
                if (!Objects.equals(userState, userServerState.userState)) {
                    if (socket == null) {
                        System.out.println("Socket is null");
                    } else if (socket.isClosed()) {
                        System.out.println("Socket is closed");
                    } else if (socket.isOutputShutdown()) {
                        System.out.println("Socket output is shutdown");
                    } else {
                        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                            out.writeInt(Constants.OpCode.USER_STATE);
                            out.writeObject(userServerState.userState);
                            out.flush();
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    UserState getUserState() {
        return userState;
    }
}
