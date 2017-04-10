package game;

import demo.DemoGame;
import engine.base.CoreEngine;
import org.lwjgl.LWJGLUtil;

import java.io.File;

/**
 * Created by Maciek on 12.07.2016.
 */
public class GameLauncher {
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

        new CoreEngine(
                //new TestGame()
                new DemoGame()
                );

        // TODO: Jeśli nie połączy z serwerem, to nie włączy gry
    }
}