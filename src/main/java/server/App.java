package server;

import java.io.IOException;


public class App {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startBroadcasting();
            server.listenOn(Constants.DEFAULT_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
