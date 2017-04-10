package demo.Tasks;

import engine.network.WrongOpCodeException;
import javafx.concurrent.Task;
import server.Constants;
import server.UserState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.Semaphore;

/**
 * Created by stasbar on 11.04.2017.
 */
@SuppressWarnings("Duplicates")
public class BroadcastListener extends Task<UserState> {
    private ObjectInputStream inputStream;
    private Semaphore semaphore = new Semaphore(1);

    public BroadcastListener(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    protected UserState call() throws Exception {
        try {
            while (!Thread.interrupted()) {

                semaphore.acquire();
                int opcode = inputStream.readInt();

                if (opcode == Constants.OpCode.USER_STATE) {
                    UserState userState = (UserState) inputStream.readObject();
                    semaphore.release();
                    return userState;
                } else
                    throw new WrongOpCodeException(Constants.OpCode.USER_STATE, opcode);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
