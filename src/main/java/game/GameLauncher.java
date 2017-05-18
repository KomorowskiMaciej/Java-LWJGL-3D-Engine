package game;

import engine.base.CoreEngine;
import org.lwjgl.LWJGLUtil;
import sun.misc.IOUtils;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Maciek on 12.07.2016.
 * @author Dominik Kinal <kinaldominik@gmail.com>
 */
public class GameLauncher {

    public static String IP = "127.0.0.1";

    public static void main(String[] args) {
        try (FileInputStream inputStream = new FileInputStream("ip.txt")) {
            StringBuilder str = new StringBuilder();
            int content;
            while ((content = inputStream.read()) != -1) {
                // convert to char and display it
                str.append((char) content);
            }
            IP = str.toString();

        } catch(IOException e) {
            List<String> lines = Collections.singletonList("127.0.0.1");
            Path file = Paths.get("ip.txt");
            try {
                Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException ex) {
            }
        }

        System.out.println("IP: " + IP);

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