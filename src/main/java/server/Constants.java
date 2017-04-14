package server;

/**
 * Created by stasbar on 24.03.2017.
 */
public class Constants {
    static final int MAP_X = 800;
    static final int MAP_Y = 800;
    static final int DEFAULT_PORT = 1234;
    static final float DEFAULT_X_SPAWN_COORDINATE = 400f;
    static final float DEFAULT_Y_SPAWN_COORDINATE = 0f;
    static final float DEFAULT_Z_SPAWN_COORDINATE = 400f;
    static final int DEFAULT_HP = 100;
    static final int SERVER_BROADCAST_RATE_MILLISECOND = 16;

    public class OpCode {
        public static final int USER_LOGIN = 0;
        public static final int USER_STATE = 1;
        public static final int USER_LOGOUT = 2;
    }
}
