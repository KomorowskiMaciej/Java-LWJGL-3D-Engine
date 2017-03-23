package Engine.modules.gameObject.gameObjectComponents;

import Engine.base.MasterRenderer;
import Engine.modules.gameObject.GameObject;
import Engine.modules.resourceMenegment.Loader;
import Engine.modules.resourceMenegment.containers.Mesh;
import Engine.modules.resourceMenegment.containers.TerrainTexture;
import Engine.modules.resourceMenegment.containers.TerrainTexturePack;
import Engine.settings.Config;
import Engine.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Maciek on 12.07.2016.
 */
public class TerrainRendererComponent extends GameObjectComponent {

    private static final float SIZE = 800;
    private static final float MAX_HEIGHT = 40;
    private static final float MAX_PIXEL_COLOUR = 16777216;

    private Mesh m_mesh;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;

    private float[][] heights;

    public TerrainRendererComponent(GameObject gameObject, int gridX, int gridZ, TerrainTexturePack textures, TerrainTexture blendMap) {
        setGameObject(gameObject);

        texturePack = textures;
        this.blendMap = blendMap;
        getGameObject().setPosition(new Vector3f(gridX * SIZE, 0, gridZ * SIZE));
        this.m_mesh = generateTerrain("heightmap");
    }


    public void Input() {
    }

    public void Update() {
    }

    public void Render() {
        MasterRenderer.getInstance().processTerrainRenderer(this);
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public float getX() {
        return getGameObject().getPosition().x;
    }


    public float getZ() {
        return getGameObject().getPosition().z;
    }


    public Mesh getModel() {
        return m_mesh;
    }


    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - getGameObject().getPosition().x;
        float terrainZ = worldZ - getGameObject().getPosition().z;
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float answer;

        if (xCoord <= (1 - zCoord)) {
            answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ], 0), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }

        return answer;
    }

    private Mesh generateTerrain(String heightMap) {

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(Config.getResPath()+"textures/"+heightMap+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int VERTEX_COUNT = image.getHeight();

        int count = VERTEX_COUNT * VERTEX_COUNT;
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT * 1)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j, i, image);
                vertices[vertexPointer * 3 + 1] = height;
                heights[j][i] = height;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, image);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return Loader.getInstance().loadToVAO(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormal(int x, int z, BufferedImage image) {
        float heightL = getHeight(x - 1, z, image);
        float heightR = getHeight(x + 1, z, image);
        float heightD = getHeight(x, z - 1, image);
        float heightU = getHeight(x, z + 1, image);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    private float getHeight(int x, int z, BufferedImage image) {
        if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
            return 0;
        }
        float height = image.getRGB(x, z);
        height += MAX_PIXEL_COLOUR / 2f;
        height /= MAX_PIXEL_COLOUR / 2f;
        height *= MAX_HEIGHT;
        return height;
    }

}
