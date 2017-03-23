package engine.modules.resourceMenegment.containers;

public class Model {

    private Mesh mesh;
    private Texture texture;
    private boolean disableCulling = false;
    private int numberOfRows = 1;


    public Model(Mesh model, Texture texture, int number) {
        this.mesh = model;
        this.texture = texture;
        this.numberOfRows = number;
    }

    public Model(Mesh model, Texture texture) {
        this.mesh = model;
        this.texture = texture;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public Mesh getRawModel() {
        return mesh;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isDisableCulling() {
        return disableCulling;
    }

    public Model setDisableCulling(boolean disableCulling) {
        this.disableCulling = disableCulling;
        return this;
    }
}
