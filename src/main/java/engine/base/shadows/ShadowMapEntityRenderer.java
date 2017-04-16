package engine.base.shadows;

import engine.base.MasterRenderer;
import engine.base.gameObject.gameObjectComponents.MeshRendererComponent;
import engine.base.resourceManagment.containers.model.Mesh;
import engine.base.resourceManagment.containers.model.Model;
import engine.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;
import java.util.Map;

public class ShadowMapEntityRenderer {

    private Matrix4f projectionViewMatrix;
    private ShadowShader shader;

    protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
        this.shader = shader;
        this.projectionViewMatrix = projectionViewMatrix;
    }

    protected void render(Map<Model, List<MeshRendererComponent>> entities) {
        for (Model model : entities.keySet()) {
            Mesh rawModel = model.getRawModel();
            bindModel(rawModel);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
            if (model.getTexture().isHasTransparency())
                MasterRenderer.disableCulling();
            for (MeshRendererComponent entity : entities.get(model)) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);
            }
            if (model.getTexture().isHasTransparency())
                MasterRenderer.enableCulling();
        }

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    private void bindModel(Mesh rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
    }

    private void prepareInstance(MeshRendererComponent entity) {
        Matrix4f modelMatrix = Maths.createTransformationMatrix(entity.getGameObject().getPosition(),
                entity.getGameObject().getRotation().x, entity.getGameObject().getRotation().y, entity.getGameObject().getRotation().z, entity.getGameObject().getScale());
        Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
        shader.loadMvpMatrix(mvpMatrix);
        shader.loadOffset(new Vector2f(entity.getTextureXOffset(), entity.getTextureYOffset()));
        shader.loadNumberOfRows(entity.getTexture().getNumberOfRows());
    }

}
