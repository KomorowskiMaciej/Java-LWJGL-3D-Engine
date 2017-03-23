package Engine.modules.gameObject.gameObjectComponents;

import Engine.base.MasterRenderer;
import Engine.modules.resourceMenegment.containers.Model;

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

    public void Input() {
    }

    public void Update() {
    }

    public void Render() {
        MasterRenderer.getInstance().processObjectRenderer(this);
    }

    public Model getTexture() {
        return model;
    }
}
