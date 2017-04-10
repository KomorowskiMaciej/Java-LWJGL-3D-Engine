package demo;

import demo.Tasks.BroadcastListener;
import demo.Tasks.FakeMover;
import demo.Tasks.LoginTask;
import engine.base.Game;
import engine.modules.gameObject.GameObject;
import server.UserState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Created by Maciek on 12.07.2016.
 */
@SuppressWarnings("Duplicates")
public class DemoGame extends Game {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private HashMap<String, GameObject> players = new HashMap<>();
    private UserState myPlayerState;

    public DemoGame() {
        try {
            setUpMultiplayer();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void setUpMultiplayer() throws ExecutionException {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 1234);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Future<UserState> userStateFuture = (Future<UserState>) executorService.submit(new LoginTask(out, in));
            myPlayerState = userStateFuture.get();

            scheduledExecutorService.scheduleAtFixedRate(new FakeMover(out, myPlayerState), 0
                    , 20, TimeUnit.MILLISECONDS);

            executorService.submit(new BroadcastListener(in));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }


}
