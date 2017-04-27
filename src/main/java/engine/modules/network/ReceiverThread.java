package engine.modules.network;

import server.Constants;
import server.GamePackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
* @author Dominik Kinal <kinaldominik@gmail.com>
*/
public class ReceiverThread implements Runnable {
    private ObjectInputStream inputStream;
    private ConcurrentLinkedQueue<NetworkEvent> queue;
    private MonitorThread monitor;

    public ReceiverThread(ObjectInputStream inputStream, ConcurrentLinkedQueue<NetworkEvent> queue) {
        this.inputStream = inputStream;
        this.queue = queue;
        this.monitor = null;
    }

    public ReceiverThread(ObjectInputStream inputStream, ConcurrentLinkedQueue<NetworkEvent> queue, MonitorThread monitor) {
        this.inputStream = inputStream;
        this.queue = queue;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                GamePackage gamePackage = (GamePackage) inputStream.readObject();
                if (gamePackage.getOpCode() == Constants.OpCode.USER_STATE || gamePackage.getOpCode() == Constants.OpCode.USER_LOGOUT) {
                    NetworkEvent event = new NetworkEvent<>(gamePackage.getOpCode(), gamePackage);

                    queue.add(event);

                    if(monitor != null)
                        monitor.received.add(event);
                } else
                    throw new IllegalStateException("Wrong opcode.");

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
