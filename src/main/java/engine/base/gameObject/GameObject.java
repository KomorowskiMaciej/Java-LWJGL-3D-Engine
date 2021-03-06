package engine.base.gameObject;

import engine.base.gameObject.gameObjectComponents.GameObjectComponent;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

/**
 * Created by Maciek on 12.07.2016.
 */
public final class GameObject {

    private ArrayList<GameObject> m_children;
    private ArrayList<GameObjectComponent> m_components;
    private Vector3f m_position = new Vector3f();
    private Vector3f m_rotation = new Vector3f();
    private Vector3f m_scale = new Vector3f();

    public GameObject(Vector3f position, Vector3f rotation, Vector3f scale) {
        m_children = new ArrayList<GameObject>();
        m_components = new ArrayList<GameObjectComponent>();
        m_position = position;
        m_rotation = rotation;
        m_scale = scale;
    }

    public GameObject(Vector3f position, Vector3f rotation) {
        this(position, rotation, new Vector3f(1, 1, 1));
    }

    public GameObject(Vector3f position) {
        this(position, new Vector3f(0, 0, 0));
    }

    public GameObject() {
        this(new Vector3f(0, 0, 0));
    }

    public GameObject AddComponent(GameObjectComponent component) {
        m_components.add(component);
        component.setGameObject(this);
        return this;
    }

    public void input() {

        for (GameObjectComponent component : m_components)
            component.input();
    }


    public void init() {

        for (GameObjectComponent component : m_components)
            component.init();
    }

    public void update() {
        for (GameObjectComponent component : m_components)
            component.update();
    }

    public void render() {
        for (GameObjectComponent component : m_components)
            component.render();
    }

    public void increasePosition(float dx, float dy, float dz) {
        this.m_position.x += dx;
        this.m_position.y += dy;
        this.m_position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.m_rotation.x += dx;
        this.m_rotation.y += dy;
        this.m_rotation.z += dz;
    }

    public Vector3f getPosition() {
        return m_position;
    }

    public void setPosition(Vector3f position) {
        m_position = position;
    }

    public Vector3f getRotation() {
        return m_rotation;
    }

    public void setRotation(Vector3f m_rotation) {
        this.m_rotation = m_rotation;
    }

    public Vector3f getScale() {
        return m_scale;
    }

    public void setScale(Vector3f scale) {
        this.m_scale = scale;
    }

    public <T> T getComponent(Class<T> asd) {
        for (GameObjectComponent component : m_components) {
            if (asd.isInstance(component))
                return (T) component;
        }
        return null;
    }
}
