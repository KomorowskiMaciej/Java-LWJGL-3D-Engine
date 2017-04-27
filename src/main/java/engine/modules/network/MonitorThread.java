package engine.modules.network;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
* @author Dominik Kinal <kinaldominik@gmail.com>
*/
public class MonitorThread implements Runnable {

    public static volatile float packagesReceivedPerSecond;
    public static volatile float packagesSentPerSecond;

    public ConcurrentLinkedQueue<NetworkEvent> received = new ConcurrentLinkedQueue<>();
    public ConcurrentLinkedQueue<NetworkEvent> sent = new ConcurrentLinkedQueue<>();

    private int packagesReceivedThisSecond = 0;
    private int packagesSentThisSecond = 0;
    private long secondStarted = 0;

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            long current = System.currentTimeMillis();

            if(current - secondStarted > 1000) {
                packagesSentPerSecond = packagesSentThisSecond;
                packagesSentThisSecond = 0;
                packagesReceivedPerSecond = packagesReceivedThisSecond;
                packagesReceivedThisSecond = 0;
                secondStarted = current;
            } else {
                while (received.size() > 0) {
                    NetworkEvent event = received.poll();
                    packagesReceivedThisSecond++;
                }
                while (sent.size() > 0) {
                    NetworkEvent event = sent.poll();
                    packagesSentThisSecond++;
                }
            }
        }
    }
}
