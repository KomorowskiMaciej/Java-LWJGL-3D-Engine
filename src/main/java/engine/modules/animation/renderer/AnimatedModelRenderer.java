package engine.modules.animation.renderer;

import engine.base.resourceManagment.containers.animation.AnimatedModel;
import org.lwjgl.opengl.GL11;

public class AnimatedModelRenderer {

	private AnimatedModelShader shader;

	public AnimatedModelRenderer() {
		this.shader = new AnimatedModelShader();
	}

	public void render(AnimatedModel entity) {
		entity.getTexture().bindToUnit(0);
		entity.getModel().bind(0, 1, 2, 3, 4);
		shader.jointTransforms.loadMatrixArray(entity.getJointTransforms());
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		entity.getModel().unbind(0, 1, 2, 3, 4);
	}

	public void cleanUp() {
		shader.cleanUp();
	}
}
