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

        new CoreEngine(new TestGame()); // TODO: Jeśli nie połączy z serwerem, to nie włączy gry
    }
}