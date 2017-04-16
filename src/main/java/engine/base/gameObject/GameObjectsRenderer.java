package engine.base.gameObject;

import engine.base.MasterRenderer;
import engine.base.gameObject.gameObjectComponents.MeshRendererComponent;
import engine.base.resourceManagment.containers.model.Mesh;
import engine.base.resourceManagment.containers.model.Model;
import engine.base.resourceManagment.containers.texture.Texture;
import engine.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import java.util.List;
import java.util.Map;

public class GameObjectsRenderer {

    private GameObjectShader shader;

    public GameObjectsRenderer(GameObjectShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(Map<Model, List<MeshRendererComponent>> renderers, Matrix4f toShadowSpace) {
        shader.loadToShadowSpaceMatrix(toShadowSpace);
        for (Model model : renderers.keySet()) {
            prepareTexturedModel(model);
            List<MeshRendererComponent> batch = renderers.get(model);
            for (MeshRendererComponent renderer : batch) {
                prepareInstance(renderer);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModel(Model model) {
        Mesh rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        Texture texture = model.getTexture();
        if (texture.isHasTransparency() || model.isDisableCulling())
            MasterRenderer.disableCulling();
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(MeshRendererComponent renderer) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(renderer.getGameObject().getPosition(),
                renderer.getGameObject().getRotation().getX(),
                renderer.getGameObject().getRotation().getY(),
                renderer.getGameObject().getRotation().getZ(),
                renderer.getGameObject().getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(renderer.getTextureXOffset(), renderer.getTextureYOffset());
        shader.loadNumberOfRows(renderer.getTexture().getNumberOfRows());
    }

}
