package engine.modules.animation.renderer;

import engine.modules.animation.shader.*;
import engine.toolbox.MyFile;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class AnimatedModelShader extends ShaderProgram {

	private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
	private static final int DIFFUSE_TEX_UNIT = 0;

	private static final MyFile VERTEX_SHADER = new MyFile("res/shaders/animatedEntityVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("res/shaders/animatedEntityFragment.glsl");

	protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	protected UniformMatrix transformMatrix = new UniformMatrix("transformMatrix");
	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformMat4Array jointTransforms = new UniformMat4Array("jointTransforms", MAX_JOINTS);
	private UniformSampler diffuseMap = new UniformSampler("diffuseMap");

	/**
	 * Creates the shader program for the {@link AnimatedModelRenderer} by
	 * loading up the vertex and fragment shader code files. It also gets the
	 * location of all the specified uniform variables, and also indicates that
	 * the diffuse texture will be sampled from texture unit 0.
	 */
	public AnimatedModelShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position", "in_textureCoords", "in_normal", "in_jointIndices",
				"in_weights");
		super.storeAllUniformLocations(projectionMatrix, viewMatrix, transformMatrix, diffuseMap, lightDirection, jointTransforms);
		connectTextureUnits();
	}

	/**
	 * Indicates which texture unit the diffuse texture should be sampled from.
	 */
	private void connectTextureUnits() {
		super.start();
		diffuseMap.loadTexUnit(DIFFUSE_TEX_UNIT);
		super.stop();
	}

	public UniformMatrix getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setProjectionMatrix(Matrix4f projectionViewMatrix) {this.projectionMatrix.loadMatrix(projectionViewMatrix);}

	public UniformMatrix getViewMatrix() {return viewMatrix;}

	public void setViewMatrix(Matrix4f viewMatrix) {
		this.viewMatrix.loadMatrix(viewMatrix);
	}

	public UniformMatrix getTransformMatrix() {
		return transformMatrix;
	}

	public void setTransformMatrix(Matrix4f transformMatrix) {
		this.transformMatrix.loadMatrix(transformMatrix);
	}

	public UniformVec3 getLightDirection() {
		return lightDirection;
	}

	public void setLightDirection(Vector3f lightDirection) {
		this.lightDirection.loadVec3(lightDirection);
	}

	public UniformMat4Array getJointTransforms() {
		return jointTransforms;
	}

	public void setJointTransforms(UniformMat4Array jointTransforms) {
		this.jointTransforms = jointTransforms;
	}
}
