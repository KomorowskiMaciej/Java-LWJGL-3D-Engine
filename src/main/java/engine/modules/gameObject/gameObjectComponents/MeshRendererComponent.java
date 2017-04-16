package engine.modules.gameObject.gameObjectComponents;

import engine.base.MasterRenderer;
import engine.modules.resourceManagment.containers.Model;

/**
 * Created by Maciek on 12.07.2016.
 */
public class MeshRendererComponent extends GameObjectComponent {

    private Model model;
    private int textureIndex = 0;

    public MeshRendererComponent(Model model, int textureIndex) {
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
        MasterRenderer.getInstance().processObjectRenderer(this);
    }

    public Model getTexture() {
        return model;
    }
}
