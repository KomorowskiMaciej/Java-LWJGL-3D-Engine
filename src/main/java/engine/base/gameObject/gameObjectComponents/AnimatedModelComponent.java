package engine.base.gameObject.gameObjectComponents;

import engine.base.MasterRenderer;
import engine.base.resourceManagment.containers.animation.AnimatedModel;
import engine.base.resourceManagment.containers.model.Model;

/**
 * Created by Maciek on 12.07.2016.
 */
public class AnimatedModelComponent extends GameObjectComponent {

    private AnimatedModel model;
    private int textureIndex = 0;

    public AnimatedModelComponent(AnimatedModel model) {
        this.model = model;
        this.textureIndex = textureIndex;
    }

    public AnimatedModelComponent(AnimatedModel model, int textureIndex) {
        this.model = model;
        this.textureIndex = textureIndex;
    }

    @Override
    public void init() {

    }

    public void input() {
    }

    public void update() {
    }

    public void render() {
        MasterRenderer.getInstance().processAnimatedModelRenderer(this);
    }

    public AnimatedModel getModel() {
        return model;
    }
}
