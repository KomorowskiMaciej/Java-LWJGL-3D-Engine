package engine.base.resourceManagment.animationLoader;

import engine.base.resourceManagment.containers.texture.Texture;
import engine.base.resourceManagment.containers.animation.AnimatedModel;
import engine.base.resourceManagment.containers.animation.Joint;
import engine.base.resourceManagment.colladaParser.colladaLoader.ColladaLoader;
import engine.base.resourceManagment.colladaParser.dataStructures.AnimatedModelData;
import engine.base.resourceManagment.colladaParser.dataStructures.JointData;
import engine.base.resourceManagment.colladaParser.dataStructures.MeshData;
import engine.base.resourceManagment.colladaParser.dataStructures.SkeletonData;
import engine.base.resourceManagment.containers.model.Mesh;
import engine.base.resourceManagment.containers.file.ResourceFile;

import static engine.toolbox.Utils.createJoints;

public class AnimatedModelLoader {

	public static AnimatedModel loadEntity(ResourceFile modelFile, ResourceFile textureFile) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, 3);
		Mesh model = createVao(entityData.getMeshData());
		Texture texture = loadTexture(textureFile);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedModel(model, texture, headJoint, skeletonData.jointCount, skeletonData);
	}

	private static Texture loadTexture(ResourceFile textureFile) {
		Texture diffuseTexture = Texture.newTexture(textureFile).anisotropic().create();
		return diffuseTexture;
	}

	private static Mesh createVao(MeshData data) {
		Mesh vao = Mesh.create();
		vao.bind();
		vao.createIndexBuffer(data.getIndices());
		vao.createAttribute(0, data.getVertices(), 3);
		vao.createAttribute(1, data.getTextureCoords(), 2);
		vao.createAttribute(2, data.getNormals(), 3);
		vao.createIntAttribute(3, data.getJointIds(), 3);
		vao.createAttribute(4, data.getVertexWeights(), 3);
		vao.unbind();
		return vao;
	}

}
