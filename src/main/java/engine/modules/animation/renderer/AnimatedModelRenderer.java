package engine.modules.animation.renderer;

import engine.modules.animation.animatedModel.AnimatedModel;
import engine.modules.gameObject.gameObjectComponents.CameraBaseComponent;
import engine.toolbox.OpenGlUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
/**
 * 
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 * 
 * @author Karl
 *
 */
public class AnimatedModelRenderer {

	private AnimatedModelShader shader;
	/**
	 * Initializes the shader program used for rendering animated models.
	 */
	public AnimatedModelRenderer() {
		this.shader = new AnimatedModelShader();
	}

	/**
	 * Renders an animated entity. The main thing to note here is that all the
	 * joint transforms are loaded up to the shader to a uniform array. Also 5
	 * attributes of the VAO are enabled before rendering, to include joint
	 * indices and weights.
	 */
	public void render(AnimatedModel entity) {
		entity.getTexture().bindToUnit(0);
		entity.getModel().bind(0, 1, 2, 3, 4);
		shader.jointTransforms.loadMatrixArray(entity.getJointTransforms());
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		entity.getModel().unbind(0, 1, 2, 3, 4);
	}

	/**
	 * Deletes the shader program when the game closes.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}
}
