package engine.base;

import engine.base.gameObject.GameObject;
import engine.base.gameWindow.Window;
import engine.modules.input.Input;
import engine.modules.input.MousePicker;
import org.lwjgl.opengl.Display;

/**
 * Created by Maciek on 12.07.2016.
 */
public class CoreEngine {

    private boolean m_isRunning;
    private Game m_game;

    public CoreEngine(Game game) {

        m_game = game;
        Window.createDisplay();
        m_game.init();


        MasterRenderer renderer = MasterRenderer.getInstance();
        MousePicker picker = new MousePicker(m_game.getCamera(), renderer.getProjectionMatrix(), m_game.getTerrain(), m_game.getGameObjectList());
        Input.setMousePicker(picker);

        for (GameObject gameObject : m_game.getGameObjectList())
            gameObject.init();

        while (!Display.isCloseRequested()) {
            m_game.input();

            // UPDATES

            for (GameObject gameObject : m_game.getGameObjectList())
                gameObject.input();
            m_game.update();

            for (GameObject gameObject : m_game.getGameObjectList())
                gameObject.update();

            picker.update();


            // RENDER

            for (GameObject gameObject : m_game.getGameObjectList())
                gameObject.render();
            renderer.render(m_game.getLights(), m_game.getGuiTextures(), m_game.getAnimatedModels());


            Window.updateDisplay();
        }

        renderer.cleanUp();
        m_game.cleanUp();
        Window.closeDisplay();

    }
}