package engine.base.resourceManagment.containers.texture;

import engine.base.resourceManagment.textureLoader.TextureBuilder;
import engine.base.resourceManagment.containers.file.ResourceFile;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Texture {

    private int textureID;

    private float shineDamper = 1;
    private float reflectivity = 0;

    private boolean hasTransparency = false;
    private boolean useFakeLighting = false;

    public Texture(int texture) {
        this.textureID = texture;
        this.type = GL11.GL_TEXTURE_2D;
        this.size = 0;
    }

    public int getID() {
        return textureID;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public boolean isHasTransparency() {
        return hasTransparency;
    }

    public Texture setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
        return this;
    }

    public boolean isUseFakeLighting() {
        return useFakeLighting;
    }

    public void setUseFakeLighting(boolean useFakeLighting) {
        this.useFakeLighting = useFakeLighting;
    }


    public final int size;
    private final int type;

    public Texture(int textureId, int size) {
        this.textureID = textureId;
        this.size = size;
        this.type = GL11.GL_TEXTURE_2D;
    }

    public Texture(int textureId, int type, int size) {
        this.textureID = textureId;
        this.size = size;
        this.type = type;
    }

    public void bindToUnit(int unit) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        GL11.glBindTexture(type, textureID);
    }

    public void delete() {
        GL11.glDeleteTextures(textureID);
    }

    public static TextureBuilder newTexture(ResourceFile textureFile) {
        return new TextureBuilder(textureFile);
    }
}
