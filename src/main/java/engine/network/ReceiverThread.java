package engine.network;

import server.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ReceiverThread implements Runnable {
    private Socket socket;

    public ReceiverThread(Socket socket) throws IOException {
        this.socket = socket;
    }


    @Override
    public void run() {

        try(ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            while (!Thread.interrupted() && socket.isConnected() && !socket.isClosed() && !socket.isInputShutdown()) {
                int opcode = inputStream.readInt();

                switch (opcode) {
                   case Constants.OpCode.USER_STATE: {
                        System.out.println("USERSTATE");
                        UserState state = (UserState) inputStream.readObject();

                        EventQueue.queue.add(new NetworkEvent<>(NetworkEvent.PLAYER_MOVE, state));
                        break;
                    }
                    case Constants.OpCode.LOGIN: {
                        System.out.println("LOGIN");
                        UserState id = (UserState) inputStream.readObject();

                        EventQueue.queue.add(new NetworkEvent<>(NetworkEvent.LOGIN, id));
                        break;
                    }

                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
