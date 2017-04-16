package engine.base.gameObject.gameObjectComponents;

import engine.base.gameObject.GameObject;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Maciek on 13.07.2016.
 */
public class ThirdPersonCamera extends CameraBaseComponent {


    private float distanceFromPlayer = 40;
    private float angleAroundPlayer = 0;


    private GameObject player;

    public ThirdPersonCamera(GameObject playerObject) {

        player = playerObject;

    }

    @Override
    public void update() {
        calculateZoom();
        calculatePitch();
        float horizontalDistance = calculateHorizontalDistance();
        float vecticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, vecticalDistance, player, angleAroundPlayer);
        getGameObject().getRotation().y = 180 - (player.getRotation().y + angleAroundPlayer);
    }

    public void input() {
    }

    public void render() {
    }

    public Vector3f getPosition() {
        return getGameObject().getPosition();
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
