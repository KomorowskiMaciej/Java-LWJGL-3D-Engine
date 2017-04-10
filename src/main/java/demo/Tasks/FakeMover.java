package demo.Tasks;

import org.lwjgl.util.vector.Vector3f;
import server.Constants;
import server.UserState;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.Semaphore;

/**
 * Created by stasbar on 11.04.2017.
 */
public class FakeMover implements Runnable {
    private ObjectOutputStream out;
    private UserState myPlayerState;
    private Semaphore semaphore = new Semaphore(1);

    public FakeMover(ObjectOutputStream out, UserState userState) {
        this.out = out;
        this.myPlayerState = userState;
    }


    @Override
    public void run() {
        try {
            Vector3f currentPosition = myPlayerState.getPosition();
            myPlayerState.setPosition(
                    new Vector3f(currentPosition.getX() > 400 ? currentPosition.getX() + 0.01f : currentPosition.getX() + 0.01f
                            , currentPosition.getZ() > 400 ? currentPosition.getZ() + 0.01f : currentPosition.getZ() + 0.01f
                            , currentPosition.getY()));

            semaphore.acquire();

            out.writeInt(Constants.OpCode.USER_STATE);
            out.writeObject(myPlayerState);
            out.flush();
            semaphore.release();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
