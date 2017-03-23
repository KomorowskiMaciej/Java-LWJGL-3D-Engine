package engine.modules.input;

import engine.modules.gameObject.GameObject;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Maciek on 14.07.2016.
 */
public class Input {

    private static MousePicker mousePicker;

    public static void setMousePicker(MousePicker mousePickerObj) {
        mousePicker = mousePickerObj;
    }

    public static Vector3f getCurrentRay() {
        return mousePicker.getCurrentRay();
    }

    public static Vector3f getCurrentTerrainPoint() {
        return mousePicker.getCurrentTerrainPoint();
    }

    public static GameObject getTargetObject() {
        return mousePicker.getTargerObject();
    }

}
