package engine.base.gameObject.gameObjectComponents;

import engine.base.MasterRenderer;
import engine.base.resourceManagment.containers.model.Model;

/**
 * Created by Maciek on 12.07.2016.
 */
public class ModelComponent extends GameObjectComponent {

    private Model model;
    private int textureIndex = 0;

    public ModelComponent(Model model) {
        this.model = model;
    }

    public ModelComponent(Model model, int textureIndex) {
        this.model = model;
        this.textureIndex = textureIndex;
    }


    public float getTextureXOffset() {
        int column = textureIndex % model.getNumberOfRows();
        return (float) column / (float) model.getNumberOfRows();
    }

    public float getTextureYOffset() {
        int row = textureIndex / model.getNumberOfRows();
        return (float) row / (float) model.getNumberOfRows();
    }

    @Override
    public void init() {

    }

    public void input() {
    }

    public void update() {
    }

    public void render() {
        MasterRenderer.getInstance().processModelRenderer(this);
    }

    public Model getModel() {
        return model;
    }
}
