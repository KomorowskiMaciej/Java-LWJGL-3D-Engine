package engine.network;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventQueue {
    public static ConcurrentLinkedQueue<NetworkEvent> queue = new ConcurrentLinkedQueue<>();
}
