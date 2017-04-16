package engine.modules.network;

import server.Constants;
import server.GamePackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReceiverThread implements Runnable {
    private ObjectInputStream inputStream;
    private ConcurrentLinkedQueue<NetworkEvent> queue;


    public ReceiverThread(ObjectInputStream inputStream, ConcurrentLinkedQueue<NetworkEvent> queue) {
        this.inputStream = inputStream;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                GamePackage gamePackage = (GamePackage) inputStream.readObject();
                if (gamePackage.getOpCode() == Constants.OpCode.USER_STATE) {
                    queue.add(new NetworkEvent<>(Constants.OpCode.USER_STATE, gamePackage));
                } else if (gamePackage.getOpCode() == Constants.OpCode.USER_LOGOUT){
                    queue.add(new NetworkEvent<>(Constants.OpCode.USER_LOGOUT, gamePackage));
                }else
                    throw new IllegalStateException("Wrong opcode.");

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
