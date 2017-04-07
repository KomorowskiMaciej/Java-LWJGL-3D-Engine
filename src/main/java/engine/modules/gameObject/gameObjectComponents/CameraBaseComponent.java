package engine.modules.gameObject.gameObjectComponents;

import engine.modules.gameObject.GameObject;

/**
 * Created by Maciek on 13.07.2016.
 */
public abstract class CameraBaseComponent extends GameObjectComponent {

    protected void calculateCameraPosition(float horizDistance, float verticDistance, GameObject player, float angle) {
        float theta = player.getRotation().y + angle;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        getGameObject().getPosition().x = player.getPosition().x - offsetX;
        getGameObject().getPosition().z = player.getPosition().z - offsetZ;
        getGameObject().getPosition().y = player.getPosition().y + verticDistance;
    }
}
