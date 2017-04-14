package server;

import org.lwjgl.util.vector.Vector3f;

import java.io.ObjectOutputStream;

/**
 * Created by macias on 14.04.2017.
 */
public class ServerGameObject {

    private String userID;
    private Vector3f position;
    private Vector3f rotation;
    private ObjectOutputStream out;

    ServerGameObject(String userID, ObjectOutputStream out){
        this.userID = userID;
        position = null;
        rotation = null;
        this.out = out;
    }

    ServerGameObject(String userID, Vector3f position, Vector3f rotation, ObjectOutputStream out){
        this.userID = userID;
        this.position = position;
        this.rotation = rotation;
        this.out = out;
    }

    ServerGameObject(String userID, Vector3f position, Vector3f rotation){
        this.userID = userID;
        this.position = position;
        this.rotation = rotation;
        out = null;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ObjectOutputStream getOut() {
        return out;
    }
    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }
}
