package server;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by stasbar on 24.03.2017.
 */
public class Constants {
    public class OpCode {
        public static final int USER_LOGIN = 0;
        public static final int USER_STATE = 1;
        public static final int USER_LOGOUT = 2;
    }
    static final int MAP_X = 800;
    static final int MAP_Y = 800;
    static final int DEFAULT_TCP_PORT = 1234;
    public  static final int DEFAULT_UDP_PORT = 4557;

    static final float DEFAULT_X_SPAWN_COORDINATE = 400f;
    static final float DEFAULT_Y_SPAWN_COORDINATE = 0f;
    static final float DEFAULT_Z_SPAWN_COORDINATE = 400f;
    static final int DEFAULT_HP = 100;
    static final int SERVER_BROADCAST_RATE_MILLISECOND = 60;
    public static final int DATAGRAM_PACKET_SIZE = 8*1024;
    public static boolean USE_UDP_WHERE_POSSIBLE = true;
    public static InetAddress MULTICAST_GROUP_ADDRESS;
    static{
        try {
            MULTICAST_GROUP_ADDRESS = InetAddress.getByName("230.1.1.2");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
