package engine.network;

import server.Constants;
import server.UserState;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ReceiverThread implements Runnable {
    private ObjectInputStream inputStream;


    public ReceiverThread(ObjectInputStream inputStream) {
        this.inputStream = inputStream;

    }

    @Override
    public void run() {
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

    }
}
