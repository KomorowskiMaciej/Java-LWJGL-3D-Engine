package game;

import engine.base.CoreEngine;
import engine.network.EventQueue;
import engine.network.FailedLoginException;
import engine.network.NetworkEvent;
import engine.network.ReceiverThread;
import org.lwjgl.LWJGLUtil;
import server.Constants;
import server.UserState;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Maciek on 12.07.2016.
 */
public class Main {
    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        File jgllib = null;

        switch (LWJGLUtil.getPlatform()) {
            case LWJGLUtil.PLATFORM_WINDOWS: {
                jgllib = new File("libs-windows/");
            }
            break;

            case LWJGLUtil.PLATFORM_LINUX: {
                jgllib = new File("libs-linux/");
            }
            break;

            case LWJGLUtil.PLATFORM_MACOSX: {
                jgllib = new File("libs-osx/");
            }
            break;
        }

        if (jgllib != null)
            System.setProperty("org.lwjgl.librarypath", jgllib.getAbsolutePath());

        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 1234);
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                login(out, in); // synchronous process -> block thread until successfully log in
                executorService.submit(new ReceiverThread(in)); // Start listening for server's userStates broadcasting
                new CoreEngine(new TestGame(out)); //Start game

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void login(ObjectOutputStream out, ObjectInputStream in) throws IOException {
        out.writeInt(Constants.OpCode.LOGIN);
        out.flush();

        int opCode = in.readInt();
        if (opCode == Constants.OpCode.LOGIN) {
            try {
                UserState newUserState = (UserState) in.readObject();
                EventQueue.queue.add(new NetworkEvent<>(NetworkEvent.LOGIN, newUserState));
                System.out.println("Loggned in");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else
            throw new FailedLoginException(String.format("Received wrong opcode [%d]during login process", opCode));


    }
}