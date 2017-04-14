package engine.modules.gameObject.gameObjectComponents;

import engine.base.Game;
import engine.modules.gameWindow.Window;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Maciek on 13.07.2016.
 */
public class FirstPersonMovementComponent extends GameObjectComponent {


    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 20;
    private static final float TERRAIN_HEIGHT = 0;
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private PhysicsComponent physics = null;
    private CameraBaseComponent camera;


    public FirstPersonMovementComponent(PhysicsComponent physics) {
        this.physics = physics;
    }

    @Override
    public void init() {
        camera = Game.getCamera();
    }

    @Override
    public void input() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            this.currentSpeed = RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        } else this.currentSpeed = 0;

        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else currentTurnSpeed = 0;

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            physics.Jump();
        }

    }

    @Override
    public void update() {
        getGameObject().setRotation(new Vector3f(
                0,
                180- camera.getGameObject().getRotation().getY(),
                0)
        );

        float distance = currentSpeed * Window.getDeltaTime();
        float dx = (float) (distance * Math.sin(Math.toRadians(getGameObject().getRotation().y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(getGameObject().getRotation().y)));
        getGameObject().increasePosition(dx, 0, dz);

        distance = currentTurnSpeed * Window.getDeltaTime();
        dx = (float) (distance * Math.sin(Math.toRadians(getGameObject().getRotation().y - 90)));
        dz = (float) (distance * Math.cos(Math.toRadians(getGameObject().getRotation().y - 90)));
        getGameObject().increasePosition(dx, 0, dz);
    }

    @Override
    public void render() {

    }
}
