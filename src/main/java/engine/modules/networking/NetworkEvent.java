package engine.modules.networking;

public class NetworkEvent<T> {
    public static int LOGIN = 0;
    public static int PLAYER_MOVE = 1;
    public static int PLAYER_SHOT = 2;
    public static int PLAYER_DEAD = 3;

    public int Type;
    public T Data;

    public NetworkEvent(int type, T data) {
        this.Type = type;
        this.Data = data;
    }
}
