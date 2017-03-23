package engine.modules.gameObject.gameObjectComponents;

import engine.modules.gameWindow.window;
import org.lwjgl.input.Keyboard;

/**
 * Created by Maciek on 13.07.2016.
 */
public class PlayerBaseComponent extends GameObjectComponent {


    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;

    private static final float TERRAIN_HEIGHT = 0;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;


    private PhysicsComponent physics = null;

    public PlayerBaseComponent(PhysicsComponent physics) {
        this.physics = physics;
    }

    @Override
    public void Input() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            this.currentSpeed = RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        } else this.currentSpeed = 0;

        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else currentTurnSpeed = 0;

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            physics.Jump();
        }

    }

    @Override
    public void Update() {
        getGameObject().increaseRotation(0, currentTurnSpeed * window.getDeltaTime(), 0);
        float distance = currentSpeed * window.getDeltaTime();
        float dx = (float) (distance * Math.sin(Math.toRadians(getGameObject().getRotation().y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(getGameObject().getRotation().y)));
        getGameObject().increasePosition(dx, 0, dz);
    }

    @Override
    public void Render() {

    }
}
