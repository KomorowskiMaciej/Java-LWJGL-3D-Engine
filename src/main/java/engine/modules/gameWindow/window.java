package engine.modules.gameWindow;

import engine.settings.Config;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class window {
    private static long lastFrameTime;
    private static float delta;

    public static void createDisplay() {
        ContextAttribs attribs = new ContextAttribs(3, 2)
                .withForwardCompatible(true)
                .withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(Config.getWindowWidth(), Config.getWindowHeight()));
            Display.create(new PixelFormat(), attribs);
            Display.setTitle(Config.getGameTitle());
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, Config.getWindowWidth(), Config.getWindowHeight());
        lastFrameTime = getCurrentTime();
    }

    public static void updateDisplay() {

        Display.sync(Config.getFpsCap());
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getDeltaTime() {
        return delta;
    }

    public static void closeDisplay() {

        Display.destroy();

    }

    private static long getCurrentTime() {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }

}
