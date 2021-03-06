package engine.base.shadows;

import engine.base.Game;
import engine.base.gameObject.gameObjectComponents.AnimatedModelComponent;
import engine.base.gameObject.gameObjectComponents.ModelComponent;
import engine.base.resourceManagment.containers.animation.AnimatedModel;
import engine.modules.light.Light;
import engine.base.resourceManagment.containers.model.Model;
import engine.settings.Config;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;
import java.util.Map;

public class ShadowMapMasterRenderer {

    private ShadowFrameBuffer shadowFbo;
    private ShadowShader shader;
    private ShadowBox shadowBox;
    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f lightViewMatrix = new Matrix4f();
    private Matrix4f projectionViewMatrix = new Matrix4f();
    private Matrix4f offset = createOffset();

    private ShadowMapEntityRenderer gameObjectsRenderer;

    public ShadowMapMasterRenderer() {
        shader = new ShadowShader();
        shadowBox = new ShadowBox(lightViewMatrix, Game.getCamera());
        shadowFbo = new ShadowFrameBuffer(Config.getShadowMapSize(), Config.getShadowMapSize());
        gameObjectsRenderer = new ShadowMapEntityRenderer(shader, projectionViewMatrix);
    }

    private static Matrix4f createOffset() {
        Matrix4f offset = new Matrix4f();
        offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
        offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
        return offset;
    }

    public void render(Map<Model, List<ModelComponent>> models,List<AnimatedModelComponent> animatedModels, Light sun) {
        shadowBox.update();
        Vector3f sunPosition = sun.getPosition();
        Vector3f lightDirection = new Vector3f(-sunPosition.x, -sunPosition.y, -sunPosition.z);
        prepare(lightDirection, shadowBox);
        gameObjectsRenderer.render(models);
        // TODO: cienie dla anumowanych modeli
        finish();
    }

    public Matrix4f getToShadowMapSpaceMatrix() {
        return Matrix4f.mul(offset, projectionViewMatrix, null);
    }

    public void cleanUp() {
        shader.cleanUp();
        shadowFbo.cleanUp();
    }

    public int getShadowMap() {
        return shadowFbo.getShadowMap();
    }

    protected Matrix4f getLightSpaceTransform() {
        return lightViewMatrix;
    }

    private void prepare(Vector3f lightDirection, ShadowBox box) {
        updateOrthoProjectionMatrix(box.getWidth(), box.getHeight(), box.getLength());
        updateLightViewMatrix(lightDirection, box.getCenter());
        Matrix4f.mul(projectionMatrix, lightViewMatrix, projectionViewMatrix);
        shadowFbo.bindFrameBuffer();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        shader.start();
    }

    private void finish() {
        shader.stop();
        shadowFbo.unbindFrameBuffer();
    }

    private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
        direction.normalise();
        center.negate();
        lightViewMatrix.setIdentity();
        float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
        Matrix4f.rotate(pitch, new Vector3f(1, 0, 0), lightViewMatrix, lightViewMatrix);
        float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;
        Matrix4f.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0), lightViewMatrix,
                lightViewMatrix);
        Matrix4f.translate(center, lightViewMatrix, lightViewMatrix);
    }

    private void updateOrthoProjectionMatrix(float width, float height, float length) {
        projectionMatrix.setIdentity();
        projectionMatrix.m00 = 2f / width;
        projectionMatrix.m11 = 2f / height;
        projectionMatrix.m22 = -2f / length;
        projectionMatrix.m33 = 1;
    }
}
