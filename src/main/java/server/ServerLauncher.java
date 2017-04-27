package server;

import com.google.gson.Gson;

import java.io.IOException;


public class ServerLauncher {
    public static void main(String[] args) {
        try {
            Server server = new Server(new Gson());
            
            if(Constants.USE_UDP_WHERE_POSSIBLE)
                server.startBroadcastingUdp();
            else
                server.startBroadcastingTcp();
            
            server.listenOnTcp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
