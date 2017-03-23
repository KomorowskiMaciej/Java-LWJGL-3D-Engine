package Engine.toolbox;

/**
 * Created by Maciek on 16.07.2016.
 */
public class Picker3D {
/*
    private static final int MAX_INSTANCES = 8000;
    private static final int INSTANCE_DATA_LENGTH = 4 + 4 + 4 + 4 + 2;
    private static final int MAX_BYTE_VAL = 255;

    private static final float[] VERTICES = { -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f };
    private static final int[] INDICES = { 0, 1, 3, 3, 1, 2, 3, 2, 7, 7, 2, 6, 7, 6, 4, 4, 6, 5, 4, 5, 0, 0, 5, 1, 4, 0,
            7, 7, 0, 3 };
    private static final int FBO_DOWNSCALE = 8;
    private static final int PBO_COUNT = 3;

    //private PickingShader shader;

    private int vao;
    private int vbo;
    private GameObject entity;
    private int base;
    private float step;

    private Matrix4f projectionView = new Matrix4f();
    private Matrix4f mvpMatrix = new Matrix4f();
    private Vector2f colour = new Vector2f();
    private FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH * 4);
    private int instanceCount;


    public Picker3D() {

    }


    public void update(Map<Integer, GameObject> worldEntities) {
        float[] instanceData = getInstanceData(worldEntities);
        renderInstances(instanceData);
        byte[] result = readPixelColour();
        if (noColourFound(result)) {
            entity = null;
        } else {
            pickEntity(result, worldEntities);
        }
    }

    public GameObject getPickedEntity() {
        return entity;
    }

    public void reset() {
        downloader.reset();
    }

    public void cleanUp() {
        downloader.cleanUp();
    }

    private float[] getInstanceData(Map<Integer, GameObject> worldEntities) {
        int totalEntities = worldEntities.size();
        float[] instanceData = new float[totalEntities * INSTANCE_DATA_LENGTH];
        instanceCount = 0;
        int pointer = 0;
        calculateBaseAndStep(totalEntities);
        for (Entity entity : worldEntities.values()) {
            if (entity.hasComponent(ComponentType.INFO) && entity.isVisible()) {
                pointer = storeEntityData(entity, pointer, instanceData);
            }
        }
        return instanceData;
    }

    private boolean noColourFound(byte[] colour) {
        byte blue = colour[2];
        int blueValue = convertUnsignedByte(blue);
        return blueValue > MAX_BYTE_VAL / 2;
    }

    private int storeEntityData(Entity entity, int pointer, float[] instanceData){
        encodeColour(entity.getId());
        pointer = loadMvpMatrix(entity, instanceData, pointer);
        pointer = storeColour(instanceData, pointer);
        instanceCount++;
        return pointer;
    }

    private byte[] readPixelColour() {
        fbo.bindToRead();
        byte[] res = downloader.downloadData(Mouse.getX() / FBO_DOWNSCALE, Mouse.getY() / FBO_DOWNSCALE);
        fbo.unbindFrameBuffer();
        return res;
    }

    private void pickEntity(byte[] result, Map<Integer, GameObject> worldEntities) {
        int index = decodeEntityIndex(result);
        entity = worldEntities.get(index);
        if (entity == null) {
            System.out.println(index + " gave no entity!");
        }
    }

    private int storeColour(float[] instanceData, int pointer) {
        instanceData[pointer++] = colour.x;
        instanceData[pointer++] = colour.y;
        return pointer;
    }

    private int convertUnsignedByte(byte b) {
        return (b & 0xFF);
    }

    private void calculateBaseAndStep(int total) {
        base = (int) (Math.floor(Math.sqrt(50000)) + 1);
        step = 1f / (float) base;
    }

    private void encodeColour(int index) {
        int r = index / base;
        int g = index % base;
        colour.set(getValue(r, step), getValue(g, step));
    }

    private float getValue(float value, float step) {
        return (value * step) + (step * 0.5f);
    }

    private int decodeEntityIndex(byte[] colour) {
        float r = convertUnsignedByte(colour[0]) / (float) MAX_BYTE_VAL;
        float g = convertUnsignedByte(colour[1]) / (float) MAX_BYTE_VAL;
        int ri = (int) (r / step);
        int gi = (int) (g / step);
        return ri * base + gi;
    }

    private int loadMvpMatrix(GameObject entity, float[] data, int pointer) {
        Matrix4f modelMat = entity.getBoundingBox().getModelMatrix();
        Matrix4f.mul(projectionView, modelMat, mvpMatrix);
        data[pointer++] = mvpMatrix.m00;
        data[pointer++] = mvpMatrix.m01;
        data[pointer++] = mvpMatrix.m02;
        data[pointer++] = mvpMatrix.m03;
        data[pointer++] = mvpMatrix.m10;
        data[pointer++] = mvpMatrix.m11;
        data[pointer++] = mvpMatrix.m12;
        data[pointer++] = mvpMatrix.m13;
        data[pointer++] = mvpMatrix.m20;
        data[pointer++] = mvpMatrix.m21;
        data[pointer++] = mvpMatrix.m22;
        data[pointer++] = mvpMatrix.m23;
        data[pointer++] = mvpMatrix.m30;
        data[pointer++] = mvpMatrix.m31;
        data[pointer++] = mvpMatrix.m32;
        data[pointer++] = mvpMatrix.m33;
        return pointer;
    }
*/
}
