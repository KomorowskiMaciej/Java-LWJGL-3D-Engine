package demo.Tasks;

import engine.network.FailedLoginException;
import javafx.concurrent.Task;
import server.Constants;
import server.UserState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Semaphore;

/**
 * Created by stasbar on 11.04.2017.
 */
@SuppressWarnings("Duplicates")
public class LoginTask extends Task<UserState> {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Semaphore semaphore = new Semaphore(1);

    public LoginTask(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    @Override
    protected UserState call() throws Exception {
        try {
            semaphore.acquire();
            out.writeInt(Constants.OpCode.LOGIN);
            out.flush();
            int opCode = in.readInt();
            if (opCode == Constants.OpCode.LOGIN) {
                try {
                    System.out.println("Loggned in");
                    UserState userState = (UserState) in.readObject();
                    semaphore.release();
                    return userState;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else
                throw new FailedLoginException(String.format("Received wrong opcode [%d] during login process", opCode));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
