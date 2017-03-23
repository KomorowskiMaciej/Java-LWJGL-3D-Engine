package engine.modules.gameObject.gameObjectComponents;

import engine.modules.gameObject.GameObject;

/**
 * Created by Maciek on 12.07.2016.
 */
public abstract class GameObjectComponent {

    private GameObject m_parent;


    public abstract void Input();

    public abstract void Update();

    public abstract void Render();


    public GameObject getGameObject() {
        return m_parent;
    }

    public void setGameObject(GameObject gameObject) {
        m_parent = gameObject;
    }
}
