package engine.modules.network;

/**
* @author Dominik Kinal <kinaldominik@gmail.com>
*/
public class NetworkEvent<T> {
    public int type;
    public T data;

    NetworkEvent(int type, T data) {
        this.type = type;
        this.data = data;
    }
}
