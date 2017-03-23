package engine.modules.gameObject.gameObjectComponents;

import engine.modules.gameObject.GameObject;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Maciek on 13.07.2016.
 */
public class FirstPersonCamera extends CameraBaseComponent {

    private float distanceFromPlayer = 40;
    private float angleAroundPlayer = 0;


    private GameObject player;

    public FirstPersonCamera(GameObject playerObject) {

        player = playerObject;

    }

    @Override
    public void Update() {
        calculateZoom();
        calculatePitch();
        float horizontalDistance = calculateHorizontalDistance();
        float vecticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, vecticalDistance);
        getGameObject().getRotation().y = 180 - (player.getRotation().y + angleAroundPlayer);
    }

    public void Input() {
    }

    public void Render() {
    }

    public Vector3f getPosition() {
        return getGameObject().getPosition();
    }


    private void calculateCameraPosition(float horizDistance, float verticDistance) {

        float theta = player.getRotation().y + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        getGameObject().getPosition().x = player.getPosition().x - offsetX;
        getGameObject().getPosition().z = player.getPosition().z - offsetZ;
        getGameObject().getPosition().y = player.getPosition().y + verticDistance;

    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.03f;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            getGameObject().getRotation().x -= pitchChange;
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(getGameObject().getRotation().x)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(getGameObject().getRotation().x)));
    }
}