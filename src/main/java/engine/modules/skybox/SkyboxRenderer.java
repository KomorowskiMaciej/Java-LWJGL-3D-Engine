package engine.modules.skybox;

import engine.base.gameObject.gameObjectComponents.CameraBaseComponent;
import engine.base.resourceManagment.Loader;
import engine.base.resourceManagment.containers.model.Mesh;
import engine.settings.Config;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


public class SkyboxRenderer {

    private static final float[] VERTICES = {
            -Config.getSkyboxSize(), Config.getSkyboxSize(), -Config.getSkyboxSize(),
            -Config.getSkyboxSize(), -Config.getSkyboxSize(), -Config.getSkyboxSize(),
            Config.getSkyboxSize(), -Config.getSkyboxSize(), -Config.getSkyboxSize(),
            Config.getSkyboxSize(), -Config.getSkyboxSize(), -Config.getSkyboxSize(),
            Config.getSkyboxSize(), Config.getSkyboxSize(), -Config.getSkyboxSize(),
            -Config.getSkyboxSize(), Config.getSkyboxSize(), -Config.getSkyboxSize(),

            -Config.getSkyboxSize(), -Config.getSkyboxSize(), Config.getSkyboxSize(),
            -Config.getSkyboxSize(), -Config.getSkyboxSize(), -Config.getSkyboxSize(),
            -Config.getSkyboxSize(), Config.getSkyboxSize(), -Config.getSkyboxSize(),
            -Config.getSkyboxSize(), Config.getSkyboxSize(), -Config.getSkyboxSize(),
            -Config.getSkyboxSize(), Config.getSkyboxSize(), Config.getSkyboxSize(),
            -Config.getSkyboxSize(), -Config.getSkyboxSize(), Config.getSkyboxSize(),

            Config.getSkyboxSize(), -Config.getSkyboxSize(), -Config.getSkyboxSize(),
            Config.getSkyboxSize(), -Config.getSkyboxSize(), Config.getSkyboxSize(),
            Config.getSkyboxSize(), Config.getSkyboxSize(), Config.getSkyboxSize(),
            Config.getSkyboxSize(), Config.getSkyboxSize(), Config.getSkyboxSize(),
            Config.getSkyboxSize(), Config.getSkyboxSize(), -Config.getSkyboxSize(),
            Config.getSkyboxSize(), -Config.getSkyboxSize(), -Config.getSkyboxSize(),

            -Config.getSkyboxSize(), -Config.getSkyboxSize(), Config.getSkyboxSize(),
            -Config.getSkyboxSize(), Config.getSkyboxSize(), Config.getSkyboxSize(),
            Config.getSkyboxSize(), Config.getSkyboxSize(), Config.getSkyboxSize(),
            Config.getSkyboxSize(), Config.getSkyboxSize(), Config.getSkyboxSize(),
            Config.getSkyboxSize(), -Config.getSkyboxSize(), Config.getSkyboxSize(),
            -Config.getSkyboxSize(), -Config.getSkyboxSize(), Config.getSkyboxSize(),

            -Config.getSkyboxSize(), Config.getSkyboxSize(), -Config.getSkyboxSize(),
            Config.getSkyboxSize(), Config.getSkyboxSize(), -Config.getSkyboxSize(),
            Config.getSkyboxSize(), Config.getSkyboxSize(), Config.getSkyboxSize(),
            Config.getSkyboxSize(), Config.getSkyboxSize(), Config.getSkyboxSize(),
            -Config.getSkyboxSize(), Config.getSkyboxSize(), Config.getSkyboxSize(),
            -Config.getSkyboxSize(), Config.getSkyboxSize(), -Config.getSkyboxSize(),

            -Config.getSkyboxSize(), -Config.getSkyboxSize(), -Config.getSkyboxSize(),
            -Config.getSkyboxSize(), -Config.getSkyboxSize(), Config.getSkyboxSize(),
            Config.getSkyboxSize(), -Config.getSkyboxSize(), -Config.getSkyboxSize(),
            Config.getSkyboxSize(), -Config.getSkyboxSize(), -Config.getSkyboxSize(),
            -Config.getSkyboxSize(), -Config.getSkyboxSize(), Config.getSkyboxSize(),
            Config.getSkyboxSize(), -Config.getSkyboxSize(), Config.getSkyboxSize()
    };

    private static String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
    private static String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};

    private Mesh cube;
    private int texture;
    private int nightTexture;
    private SkyboxShader shader;

    public SkyboxRenderer(Matrix4f projectionMatrix) {
        cube = Loader.getInstance().loadToVAO(VERTICES, 3);
        texture = Loader.getInstance().loadCubeMap(TEXTURE_FILES);
        nightTexture = Loader.getInstance().loadCubeMap(NIGHT_TEXTURE_FILES);
        shader = new SkyboxShader();
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(CameraBaseComponent camera, Vector3f env_colours) {
        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadFogColour(env_colours);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void bindTextures() {
        int texture1;
        int texture2;

        texture1 = texture;
        texture2 = nightTexture;

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
        shader.loadBlendFactor(Config.getSkyboxBlend());
    }


}
