package engine.network;

public class NetworkEvent<T> {
    public static final int LOGIN = 0;
    public static final int PLAYER_MOVE = 1;
    public static final int PLAYER_SHOT = 2;
    public static final int PLAYER_DEAD = 3;

    public int Type;
    public T Data;

    public NetworkEvent(int type, T data) {
        this.Type = type;
        this.Data = data;
    }
}
