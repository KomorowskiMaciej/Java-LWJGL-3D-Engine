package engine.modules.gameObject.gameObjectComponents;

import engine.modules.gameObject.GameObject;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Maciek on 13.07.2016.
 */
public class FirstPersonCamera extends CameraBaseComponent {

    private float distanceFromPlayer = 0;
    private float angleAroundPlayer = 0;


    private GameObject player;

    public FirstPersonCamera(GameObject playerObject) {

        player = playerObject;

    }

    @Override
    public void update() {
        calculatePitch();
        float horizontalDistance = calculateHorizontalDistance();
        float vecticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, vecticalDistance, player, angleAroundPlayer);
        getGameObject().getRotation().y = 360-(angleAroundPlayer);
    }

    public void input() {
    }

    public void render() {
    }

    public Vector3f getPosition() {
        return getGameObject().getPosition();
    }

    private void calculatePitch() {
            float pitchChange = Mouse.getDY() * 0.1f;
            getGameObject().getRotation().x -= pitchChange;
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
    }

    private float calculateHorizontalDistance() {
        return 0f;
      //  return (float) (distanceFromPlayer * Math.cos(Math.toRadians(getGameObject().getRotation().x)));
    }

    private float calculateVerticalDistance() {
        return 8f;
        //return (float) (distanceFromPlayer * Math.sin(Math.toRadians(getGameObject().getRotation().x)));
    }
}