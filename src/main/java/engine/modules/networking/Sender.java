package engine.modules.networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Sender {

    private static ObjectOutputStream _stream;

    public static void init(Socket s) throws IOException {
        _stream = new ObjectOutputStream(s.getOutputStream());
    }

    public static void send(int code, Object data) {
        try {
            _stream.writeInt(code);
            _stream.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(int code) {
        try {
            _stream.writeInt(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
