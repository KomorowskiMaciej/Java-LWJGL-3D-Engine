package game;

import engine.base.CoreEngine;
import engine.modules.networking.OpCodes;
import engine.modules.networking.ReceiverThread;
import engine.modules.networking.Sender;
import org.lwjgl.LWJGLUtil;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Maciek on 12.07.2016.
 */
public class Main {
    public static void main(String[] args) {


        File jgllib = null;

        switch(LWJGLUtil.getPlatform())
        {
            case LWJGLUtil.PLATFORM_WINDOWS:
            {
                jgllib = new File("libs-windows/");
            }
            break;

            case LWJGLUtil.PLATFORM_LINUX:
            {
                jgllib = new File("libs-linux/");
            }
            break;

            case LWJGLUtil.PLATFORM_MACOSX:
            {
                jgllib = new File("libs-osx/");
            }
            break;
        }

        if(jgllib != null)
            System.setProperty("org.lwjgl.librarypath", jgllib.getAbsolutePath());

        try {
            Socket socket = new Socket("127.0.0.1", 1234);
            new Thread(new ReceiverThread(socket)).start();

            Sender.init(socket);
            Sender.send(OpCodes.LOGIN);

            new CoreEngine(new TestGame()); // TODO: Jeśli nie połączy z serwerem, to nie włączy gry

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}