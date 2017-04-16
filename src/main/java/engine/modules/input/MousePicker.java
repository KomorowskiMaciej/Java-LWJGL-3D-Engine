package engine.modules.input;

import engine.base.gameObject.GameObject;
import engine.base.gameObject.gameObjectComponents.CameraBaseComponent;
import engine.base.gameObject.gameObjectComponents.TerrainRendererComponent;
import engine.settings.Config;
import engine.toolbox.Maths;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;

public class MousePicker {

    private static final int RECURSION_COUNT = 200;
    private static final float RAY_RANGE = 600;

    private Vector3f currentRay = new Vector3f();

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private CameraBaseComponent camera;

    private TerrainRendererComponent terrain;
    private Vector3f currentTerrainPoint;
    private List<GameObject> gameObjects;
    private GameObject targerObject;

    public MousePicker(CameraBaseComponent cam, Matrix4f projection, TerrainRendererComponent terrain, List<GameObject> gameObjectList) {
        camera = cam;
        projectionMatrix = projection;
        viewMatrix = Maths.createViewMatrix(camera);
        this.terrain = terrain;
        this.gameObjects = gameObjectList;
    }

    public Vector3f getCurrentTerrainPoint() {
        return currentTerrainPoint;
    }

    public GameObject getTargerObject() {
        return targerObject;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update() {
        viewMatrix = Maths.createViewMatrix(camera);
        currentRay = calculateMouseRay();

        //terrain

        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
        } else {
            currentTerrainPoint = null;
        }

        //gameobjects
        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            targerObject = binaryGameObjectSearch(0, 0, RAY_RANGE, currentRay);
        }
    }

    private Vector3f calculateMouseRay() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
        Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalise();
        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
        Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / Display.getWidth() - 1f;
        float y = (2.0f * mouseY) / Display.getHeight() - 1f;
        return new Vector2f(x, y);
    }

    //**********************************************************

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = camera.getGameObject().getPosition();
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return Vector3f.add(start, scaledRay, null);
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            TerrainRendererComponent terrain = getTerrain(endPoint.getX(), endPoint.getZ());
            if (terrain != null) {
                return endPoint;
            } else {
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isUnderGround(Vector3f testPoint) {
        TerrainRendererComponent terrain = getTerrain(testPoint.getX(), testPoint.getZ());
        float height = 0;
        if (terrain != null) {
            height = terrain.getHeightOfTerrain(testPoint.getX(), testPoint.getZ());
        }
        if (testPoint.y < height) {
            return true;
        } else {
            return false;
        }
    }

    private GameObject binaryGameObjectSearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            GameObject gameObject = getObject(endPoint.getX(), endPoint.getY(), endPoint.getZ());
            if (gameObject != null) {
                return gameObject;
            } else {
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binaryGameObjectSearch(count + 1, start, half, ray);
        } else {
            return binaryGameObjectSearch(count + 1, half, finish, ray);
        }
    }

    private TerrainRendererComponent getTerrain(float worldX, float worldZ) {
        return terrain;
    }

    private GameObject getObject(float x, float y, float z) {
        for (GameObject object : gameObjects) {
            if (
                    object.getPosition().x < x + Config.getMousePickerRootObjectRange() &&
                            object.getPosition().x > x - Config.getMousePickerRootObjectRange() &&
                            object.getPosition().y < y + Config.getMousePickerRootObjectRange() &&
                            object.getPosition().y > y - Config.getMousePickerRootObjectRange() &&
                            object.getPosition().z < z + Config.getMousePickerRootObjectRange() &&
                            object.getPosition().z > z - Config.getMousePickerRootObjectRange()
                    )
                return object;
        }
        return null;
    }
}
