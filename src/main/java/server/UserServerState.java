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
    private ObjectOutputStream out;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    UserServerState(String userID, ObjectOutputStream out) {
        this.userState = new UserState(userID, getRandomSprawnVector(), new Vector3f(0,0,0), DEFAULT_HP);
        this.out = out;
    }

    void updateUserState(UserState userState) {
        this.userState.setPosition(userState.getPosition());
    }

    ExecutorService getExecutor() {
        return executorService;
    }

    void sendGameState(Collection<UserServerState> userStateList) {
        userStateList.parallelStream().forEach(userServerState -> {
                    try {
                        out.writeInt(Constants.OpCode.USER_STATE);
                        out.writeObject(userServerState.userState);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        });
    }
    UserState getUserState() {
        return userState;
    }
}
