package engine.modules.network;

import server.GamePackage;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
* @author Dominik Kinal <kinaldominik@gmail.com>
*/
public class Sender {

    private static MonitorThread monitorThread = null;

    public static void init(MonitorThread monitor) {
        monitorThread = monitor;
    }

    public static void send(ObjectOutputStream out, GamePackage gp) throws IOException {
        out.writeObject(gp);
        out.flush();

        if(monitorThread != null) {
            monitorThread.sent.add(new NetworkEvent<>(gp.getOpCode(), gp));
        }
    }
}
