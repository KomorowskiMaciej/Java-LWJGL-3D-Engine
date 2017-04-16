package engine.base.gameObject.gameObjectComponents;

/**
 * Created by Maciek on 13.07.2016.
 */
public class PlayerComponent extends GameObjectComponent {

    private int hp = 100;

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }


    @Override
    public void input() {

    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }
}
