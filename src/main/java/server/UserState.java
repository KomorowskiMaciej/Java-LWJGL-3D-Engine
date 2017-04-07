package server;

import org.lwjgl.util.vector.Vector3f;

import java.io.Serializable;

/**
 * Created by stasbar on 24.03.2017.
 */
public class UserState implements Serializable {
    private String userID;
    private Vector3f position;
    private Vector3f rotation;
    private int hp;

    public UserState() {
    }

    UserState(String userID, Vector3f position, Vector3f rotation, int hp) {
        this.setUserID(userID);
        this.setHp(hp);
        this.position = position;
        this.rotation = rotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserState userState = (UserState) o;

        return getUserID().equals(userState.getUserID());
    }



    @Override
    public int hashCode() {
        return getUserID().hashCode();
    }

    @Override
    public String toString() {
        return "UserState{" +
                "userID='" + getUserID() + '\'' +
                ", hp=" + getHp() +
                '}';
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

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

}
