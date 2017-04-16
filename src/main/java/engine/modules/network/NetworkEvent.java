package engine.modules.network;

public class NetworkEvent<T> {
    public int Type;
    public T Data;

    public NetworkEvent(int type, T data) {
        this.Type = type;
        this.Data = data;
    }
}
