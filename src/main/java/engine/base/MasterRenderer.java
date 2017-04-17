package engine.base;

import engine.base.gameObject.GameObject;
import engine.base.gameObject.gameObjectComponents.AnimatedModelComponent;
import engine.base.resourceManagment.containers.animation.AnimatedModel;
import engine.modules.animation.renderer.AnimatedModelRenderer;
import engine.modules.animation.renderer.AnimatedModelShader;
import engine.modules.fonts.fontMeshCreator.FontType;
import engine.modules.fonts.fontMeshCreator.GUIText;
import engine.modules.fonts.fontRendering.FontRenderer;
import engine.modules.fonts.fontRendering.TextMaster;
import engine.base.gameObject.GameObjectsRenderer;
import engine.base.gameObject.gameObjectComponents.ModelComponent;
import engine.base.gameObject.gameObjectComponents.TerrainRendererComponent;
import engine.base.gameObject.GameObjectShader;
import engine.modules.guis.GuiRenderer;
import engine.modules.guis.GuiTexture;
import engine.modules.light.Light;
import engine.base.resourceManagment.Loader;
import engine.base.resourceManagment.containers.model.Model;
import engine.base.shadows.ShadowMapMasterRenderer;
import engine.modules.skybox.SkyboxRenderer;
import engine.modules.terrain.TerrainRenderer;
import engine.modules.terrain.TerrainShader;
import engine.settings.Config;
import engine.toolbox.Maths;
import engine.toolbox.OpenGlUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.util.*;

import static engine.toolbox.OpenGlUtils.enableDepthTesting;


/*

     m   " m" " m  m
       " m " "m   " " "m m m
     "  m  " "  " "m" " " "m m
    m  m m "  " " " " "m"m"m"m"m"m
      " m " " "m"m" "m"m"m"m"m"m"m"m
    "m"m m"m" "m m"m"m"m"m"m"m"m"m"m$"m
         $  " "m"m"m"m"m"m"m"m"m"m$"  $m
         "          "m"m"m"m"m"m$"  m"m"m
         $           $"m"m"m"m$"  m"m"  "m
        m"m          m  "m"m$"  m"m"     "m
        $ $          $    $"  m"m"        "m
         "           m    "$m"m"          m"m
                    m"m     $"          m"  $
                    $ $      $        m"  m"
                    "m"       $     m"  m$$
                               $  m"  m$"  $
                                $"  m""$    $
                                 """    $    $
                                         $    $
                                          $    $
                                           $    $
*/


public class MasterRenderer {

    private static MasterRenderer _instance = null;
    private Matrix4f projectionMatrix;
    private GameObjectShader gameObjectshader = new GameObjectShader();
    private GameObjectsRenderer gameObjectsRenderer;
    private AnimatedModelRenderer animatedModelRenderer;
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    private SkyboxRenderer skyboxRenderer;
    private GuiRenderer guiRenderer;
    private FontRenderer fontRenderer;
    private FontType verdana;
    private ShadowMapMasterRenderer shadowMapRenderer;
    private Map<Model, List<ModelComponent>> modelComponents = new HashMap<>();
    private List<AnimatedModelComponent> animatedModelComponents = new ArrayList<>();
    private List<TerrainRendererComponent> terrains = new ArrayList<>();

    private MasterRenderer() {
        enableCulling();
        createProjectionMatrix();
        gameObjectsRenderer = new GameObjectsRenderer(gameObjectshader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(projectionMatrix);
        animatedModelRenderer = new AnimatedModelRenderer();
        guiRenderer = new GuiRenderer();
        fontRenderer = new FontRenderer();
        TextMaster.init();
        verdana = new FontType(Loader.getInstance().loadFontTexture("verdana"), new File("res/fonts/verdana.fnt"));
        new GUIText("Pre-alfa " + new Date(), 1.0f, verdana, new Vector2f(0.0f, 0.96f), 1f, true).setColour(1, 1, 1);
        shadowMapRenderer = new ShadowMapMasterRenderer();

    }

    public static MasterRenderer getInstance() {
        if (_instance == null)
            _instance = new MasterRenderer();
        return _instance;
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void prepare() {
        enableDepthTesting(true);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(Config.getEnvRgb().x, Config.getEnvRgb().y, Config.getEnvRgb().z, 1);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMaptexture());
    }

    public void render(List<Light> lights, List<GuiTexture> guiTextures, List<engine.base.resourceManagment.containers.animation.AnimatedModel> animatedModels) {
        renderShadowMap(lights.get(0));

        prepare();
        gameObjectshader.start();
        gameObjectshader.loadSkyColour(Config.getEnvRgb());
        gameObjectshader.loadAmbientLight(Config.getAmbientLight());
        gameObjectshader.loadLights(lights);
        gameObjectshader.loadViewMatrix(Game.getCamera());
        gameObjectshader.loadFogDencity(Config.getFogDencity());
        gameObjectshader.loadFogGradient(Config.getFogGradient());
        gameObjectshader.loadPcfCount(Config.getShadowPcfCount());
        gameObjectshader.loadMapSize(Config.getShadowMapSize());
        gameObjectsRenderer.render(modelComponents, shadowMapRenderer.getToShadowMapSpaceMatrix());
        gameObjectshader.stop();


        AnimatedModelShader animatedModelShader = new AnimatedModelShader();
        animatedModelShader.start();
        animatedModelShader.setProjectionMatrix(projectionMatrix);
        animatedModelShader.setViewMatrix(Maths.createViewMatrix(Game.getCamera()));
        animatedModelShader.setLightDirection(new Vector3f(0.2f, -0.3f, -0.8f));
        OpenGlUtils.antialias(true);
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);

        animatedModelComponents.forEach(animatedModelComponent -> {

            GameObject go = animatedModelComponent.getGameObject();
            animatedModelShader.setTransformMatrix(Maths.createTransformationMatrix(go.getPosition(),go.getRotation(), go.getScale()));

            //todo: zrobić to jakoś lepiej
            animatedModelComponent.getModel().update();

            animatedModelRenderer.render(animatedModelComponent.getModel());

        });

        animatedModelShader.stop();

        terrainShader.start();

        terrainShader.loadSkyColour(Config.getEnvRgb());
        terrainShader.loadLights(lights);
        terrainShader.loadAmbientLight(Config.getAmbientLight());
        terrainShader.loadViewMatrix(Game.getCamera());
        terrainShader.loadFogDencity(Config.getFogDencity());
        terrainShader.loadFogGradient(Config.getFogGradient());
        terrainShader.loadPcfCount(Config.getShadowPcfCount());
        terrainShader.loadMapSize(Config.getShadowMapSize());

        terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());

        terrainShader.stop();
        skyboxRenderer.render(Game.getCamera(), Config.getEnvRgb());

        //guiTextures.add(new GuiTexture(getShadowMaptexture(),new Vector2f(0.5f,0.5f), new Vector2f(0.5f,0.5f)));
        guiRenderer.render(guiTextures);
        TextMaster.render();
        terrains.clear();
        modelComponents.clear();
        animatedModelComponents.clear();
        guiTextures.clear();
    }

    public void processTerrainRenderer(TerrainRendererComponent terrain) {
        terrains.add(terrain);
    }

    public void processModelRenderer(ModelComponent renderer) {
        Model texturedMesh = renderer.getModel();
        List<ModelComponent> batch = modelComponents.get(texturedMesh);
        if (batch != null) {
            batch.add(renderer);
        } else {
            List<ModelComponent> newBatch = new ArrayList<ModelComponent>();
            newBatch.add(renderer);
            modelComponents.put(texturedMesh, newBatch);
        }
    }

    public void processAnimatedModelRenderer(AnimatedModelComponent component) {
        if(animatedModelComponents == null)
            animatedModelComponents = new ArrayList<>();
        animatedModelComponents.add(component);
    }

    public void renderShadowMap(Light sun) {
        shadowMapRenderer.render(modelComponents, animatedModelComponents, sun);
    }

    public int getShadowMaptexture() {
        return shadowMapRenderer.getShadowMap();
    }

    public void cleanUp() {
        TextMaster.cleanUp();
        gameObjectshader.cleanUp();
        terrainShader.cleanUp();
        shadowMapRenderer.cleanUp();
    }

    private void createProjectionMatrix() {
        projectionMatrix = new Matrix4f();
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(Config.getFOV() / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = Config.getFarPlane() - Config.getNearPlane();

        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((Config.getFarPlane() + Config.getNearPlane()) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * Config.getNearPlane() * Config.getFarPlane()) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
