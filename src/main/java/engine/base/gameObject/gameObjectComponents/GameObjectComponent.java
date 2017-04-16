package engine.base.gameObject.gameObjectComponents;

import engine.base.gameObject.GameObject;

/**
 * Created by Maciek on 12.07.2016.
 */
public abstract class GameObjectComponent {

    private GameObject m_parent;


    public abstract void input();

    public void init() {
    }

    public abstract void update();

    public abstract void render();


    public GameObject getGameObject() {
        return m_parent;
    }

    public void setGameObject(GameObject gameObject) {
        m_parent = gameObject;
    }
}
