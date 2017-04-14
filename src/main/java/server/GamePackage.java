package server;

import org.lwjgl.util.vector.Vector3f;

import java.io.Serializable;

/**
 * Created by stasbar on 24.03.2017.
 */
public class GamePackage implements Serializable {
    private String userID;
    private Vector3f position;
    private Vector3f rotation;
    private int hp;
    private int opCode;

    public GamePackage(int opCode) {
        this.opCode = opCode;
        this.userID = null;
        this.hp = 0;
        this.position = null;
        this.rotation = null;

    }

    GamePackage(int opCode, String userID) {
        this.opCode = opCode;
        this.userID = userID;
        this.hp = 0;
        this.position = null;
        this.rotation = null;

    }

    public GamePackage(int opCode, String userID, Vector3f position, Vector3f rotation, int hp) {
        this.opCode = opCode;
        this.userID = userID;
        this.hp = hp;
        this.position = position;
        this.rotation = rotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GamePackage userState = (GamePackage) o;

        return getUserID().equals(userState.getUserID());
    }



    @Override
    public int hashCode() {
        return getUserID().hashCode();
    }

    @Override
    public String toString() {
        return "GamePackage{" +
                "userID='" + getUserID() + '\'' +
                ", hp=" + getHp() +
                '}';
    }

    public void print(){
        System.out.println(toString());
    }


    public String getUserID() {
        return userID;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public int getHp() {
        return hp;
    }

    public int getOpCode() {
        return opCode;
    }
}
