package engine.network;

import javafx.concurrent.Task;
import server.Constants;
import server.UserState;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ReceiverThread extends Task<Void> {
    private ObjectInputStream inputStream;

    public ReceiverThread(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    protected Void call() throws Exception {
        try {
            while (!Thread.interrupted()) {
                int opcode = inputStream.readInt();
                if (opcode == Constants.OpCode.USER_STATE) {
                    //System.out.println("USER_STATE");
                    UserState state = (UserState) inputStream.readObject();
                    EventQueue.queue.add(new NetworkEvent<>(NetworkEvent.PLAYER_MOVE, state));
                } else
                    throw new WrongOpCodeException(Constants.OpCode.USER_STATE,opcode);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
