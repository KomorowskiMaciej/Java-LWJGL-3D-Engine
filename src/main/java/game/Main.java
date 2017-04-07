package game;

import engine.base.CoreEngine;
import engine.network.ReceiverThread;
import engine.network.Sender;
import org.lwjgl.LWJGLUtil;
import server.Constants;
import server.UserState;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Maciek on 12.07.2016.
 */
public class Main {
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
            Socket socket = new Socket("localhost", 1234);
            if (socket.isConnected()) {
                new Thread(new ReceiverThread(socket)).start();

                Sender.init(socket);
                Sender.send(Constants.OpCode.LOGIN);

                new CoreEngine(new TestGame()); // TODO: Jeśli nie połączy z serwerem, to nie włączy gry
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}