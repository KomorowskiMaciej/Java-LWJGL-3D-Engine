package engine.modules.network;

import com.google.gson.Gson;
import server.Constants;
import server.GamePackage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static server.Constants.DATAGRAM_PACKET_SIZE;
import static server.Constants.MULTICAST_GROUP_ADDRESS;

/**
 * Created by stasbar on 27.04.2017.
 */
public class MulticastReceiverTask implements Runnable {
    private ConcurrentLinkedQueue<NetworkEvent> queue;
    private MonitorThread monitor;
    private Gson gson;

    public MulticastReceiverTask(ConcurrentLinkedQueue<NetworkEvent> queue, MonitorThread monitor, Gson gson) {
        this.queue = queue;
        this.monitor = monitor;
        this.gson = gson;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        byte[] gamePackageBytes = new byte[DATAGRAM_PACKET_SIZE];

        try (MulticastSocket socket = new MulticastSocket(Constants.DEFAULT_UDP_PORT)) {

            socket.joinGroup(MULTICAST_GROUP_ADDRESS);
            while (!Thread.interrupted()) {

                DatagramPacket packet = new DatagramPacket(gamePackageBytes, gamePackageBytes.length);

                socket.receive(packet);
                GamePackage gamePackage;

                try (ByteArrayInputStream byteStream = new ByteArrayInputStream(gamePackageBytes);
                     ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(byteStream))) {

                    gamePackage = (GamePackage) in.readObject();

                }

                System.out.println("Received datagram userID: " + gamePackage.getUserID());
                if (gamePackage.getOpCode() == Constants.OpCode.USER_STATE || gamePackage.getOpCode() == Constants.OpCode.USER_LOGOUT) {
                    NetworkEvent event = new NetworkEvent<>(gamePackage.getOpCode(), gamePackage);
                    queue.add(event);
                    if (monitor != null)
                        monitor.received.add(event);
                } else
                    throw new IllegalStateException("Wrong opcode.");
            }
            socket.leaveGroup(MULTICAST_GROUP_ADDRESS);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MulticastReceiverTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
