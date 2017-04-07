package engine.modules.gameObject.gameObjectComponents;

import engine.modules.gameWindow.Window;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Maciek on 13.07.2016.
 */
public class PhysicsComponent extends GameObjectComponent {


    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 20;
    private boolean isInAir = false;

    private Vector3f velocity = new Vector3f(0, 0, 0);

    @Override
    public void input() {
    }

    public void Jump() {
        if (!isInAir) {
            velocity.y += JUMP_POWER;
            isInAir = true;
        }
    }


    @Override
    public void update() {


        velocity.y += GRAVITY * Window.getDeltaTime();

        getGameObject().increasePosition(velocity.x * Window.getDeltaTime(), velocity.y * Window.getDeltaTime(), velocity.z * Window.getDeltaTime());

        float terrainHeight = game.TestGame.GetTerrain().getHeightOfTerrain(getGameObject().getPosition().x, getGameObject().getPosition().z);

        if (getGameObject().getPosition().y < terrainHeight) {
            getGameObject().getPosition().y = terrainHeight;
            velocity.y = 0;
            isInAir = false;
        }


    }

    @Override
    public void render() {

    }
}
