package engine.base.resourceManagment.animationLoader;

import java.util.HashMap;
import java.util.Map;

import engine.base.resourceManagment.containers.animation.Animation;
import engine.base.resourceManagment.containers.animation.JointTransform;
import engine.base.resourceManagment.containers.animation.KeyFrame;
import engine.base.resourceManagment.containers.math.Quaternion;
import engine.base.resourceManagment.colladaParser.colladaLoader.ColladaLoader;
import engine.base.resourceManagment.colladaParser.dataStructures.AnimationData;
import engine.base.resourceManagment.colladaParser.dataStructures.JointTransformData;
import engine.base.resourceManagment.colladaParser.dataStructures.KeyFrameData;
import engine.base.resourceManagment.containers.file.ResourceFile;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class AnimationLoader {

	public static Animation loadAnimation(ResourceFile colladaFile) {
		AnimationData animationData = ColladaLoader.loadColladaAnimation(colladaFile);
		KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = createKeyFrame(animationData.keyFrames[i]);
		}
		return new Animation(animationData.lengthSeconds, frames);
	}

	private static KeyFrame createKeyFrame(KeyFrameData data) {
		Map<String, JointTransform> map = new HashMap<String, JointTransform>();
		for (JointTransformData jointData : data.jointTransforms) {
			JointTransform jointTransform = createTransform(jointData);
			map.put(jointData.jointNameId, jointTransform);
		}
		return new KeyFrame(data.time, map);
	}

	private static JointTransform createTransform(JointTransformData data) {
		Matrix4f mat = data.jointLocalTransform;
		Vector3f translation = new Vector3f(mat.m30, mat.m31, mat.m32);
		Quaternion rotation = Quaternion.fromMatrix(mat);
		return new JointTransform(translation, rotation);
	}
}
