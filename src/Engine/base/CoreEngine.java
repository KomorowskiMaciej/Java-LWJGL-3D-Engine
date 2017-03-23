package Engine.base;

import Engine.modules.gameObject.GameObject;
import Engine.modules.gameWindow.window;
import Engine.modules.input.Input;
import Engine.modules.input.MousePicker;
import org.lwjgl.opengl.Display;

/**
 * Created by Maciek on 12.07.2016.
 */
public class CoreEngine {

    private boolean m_isRunning;
    private Game m_game;

    public CoreEngine(Game game) {

        m_game = game;
        window.createDisplay();
        m_game.init();


        MasterRenderer renderer = MasterRenderer.getInstance();
        MousePicker picker = new MousePicker(m_game.getCamera(), renderer.getProjectionMatrix(), m_game.getTerrain(), m_game.getGameObjectList());
        Input.setMousePicker(picker);
        while (!Display.isCloseRequested()) {
            m_game.input();

            for (GameObject gameObject : m_game.getGameObjectList()) {
                gameObject.Input();
            }
            m_game.update();
            for (GameObject gameObject : m_game.getGameObjectList()) {
                gameObject.Update();
            }
            picker.update();

            for (GameObject gameObject : m_game.getGameObjectList()) {
                gameObject.Render();
            }

            renderer.render(m_game.getLights(), m_game.getWaters(), m_game.getGuiTextures());
            window.updateDisplay();
        }

        renderer.cleanUp();
        m_game.cleanUp();
        window.closeDisplay();

    }
}