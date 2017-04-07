package engine.modules.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ReceiverThread implements Runnable {
    private Socket socket;
    ObjectInputStream inputStream;

    public ReceiverThread(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            try {
                int opcode = inputStream.readInt();
                switch(opcode) {
                    case OpCodes.USERSTATE: {
                        UserState state = (UserState) inputStream.readObject();

                        EventQueue.Queue.add(new NetworkEvent<>(NetworkEvent.PLAYER_MOVE, state));
                        break;
                    }
                    case OpCodes.LOGIN: {
                        String id = inputStream.readUTF();

                        EventQueue.Queue.add(new NetworkEvent<>(NetworkEvent.LOGIN, id));
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
