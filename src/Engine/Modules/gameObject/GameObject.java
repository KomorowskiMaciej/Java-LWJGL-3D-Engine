package Engine.modules.gameObject;

import Engine.modules.gameObject.gameObjectComponents.GameObjectComponent;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

/**
 * Created by Maciek on 12.07.2016.
 */
public final class GameObject {

    private ArrayList<GameObject> m_children;
    private ArrayList<GameObjectComponent> m_components;
    private Vector3f m_position;
    private Vector3f m_rotation;
    private Vector3f m_scale;

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

    public void Input() {

        for (GameObjectComponent component : m_components)
            component.Input();
    }

    public void Update() {
        for (GameObjectComponent component : m_components)
            component.Update();
    }

    public void Render() {
        for (GameObjectComponent component : m_components)
            component.Render();
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

    public void setScale(Vector3f scale) {
        this.m_scale = scale;
    }

    public Vector3f getScale() {
        return m_scale;
    }

    public <T> T getComponent() {
        T data = null;
        for (GameObjectComponent component : m_components) {
            if (component.getClass().isInstance(data))
                return (T) component;
        }
        return null;
    }
}
