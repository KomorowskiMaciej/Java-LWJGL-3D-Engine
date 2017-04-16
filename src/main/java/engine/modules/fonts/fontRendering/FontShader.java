package engine.modules.fonts.fontRendering;

import engine.base.shaders.ShaderProgram;
import engine.base.shaders.UniformVec2;
import engine.base.shaders.UniformVec3;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/fontVertex.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/fontFragment.glsl";

    private UniformVec3 colour = new UniformVec3("colour");
    private UniformVec2 translation = new UniformVec2("translation");

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE, "position", "textureCoords");
        super.storeAllUniformLocations(colour,translation);
    }

    protected void loadColour(Vector3f lcolour) {
        colour.loadVec3(lcolour);
    }

    protected void loadTranslation(Vector2f ltranslation) {
        translation.loadVec2(ltranslation);
    }


}
