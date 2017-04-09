package engine.network;

import server.Constants;
import server.UserState;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by stasbar on 08.04.2017.
 */
public class UpdatePlayerState implements Runnable {
    private final ObjectOutputStream outputStream;
    UserState userState;

    public UpdatePlayerState(ObjectOutputStream outputStream, UserState userState) {
        this.outputStream = outputStream;
        this.userState = userState;
    }

    @Override
    public void run() {
        try {
            if (userState != null && userState.getUserID() == null)
                return;
            outputStream.writeInt(Constants.OpCode.USER_STATE);
            outputStream.writeObject(userState);
            outputStream.flush();
            System.out.println("Wyslano obiekt gracza lokalnego.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
