package engine.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Sender {

    private static Socket socket;
    private static ObjectOutputStream _stream;

    public static void init(Socket s) throws IOException {
        socket = s;
        _stream = new ObjectOutputStream(s.getOutputStream());
    }

    public static void send(int code, Object data) {
        System.out.println("Sent: " + code);

        try {
            _stream.writeInt(code);
            _stream.writeObject(data);
            _stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void send(int code) {
        System.out.println("Sent: " + code);

        try {
            _stream.writeInt(code);
            _stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
