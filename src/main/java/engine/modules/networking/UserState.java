package engine.modules.networking;

import org.lwjgl.util.vector.Vector3f;

public class UserState {
    private String userID;
    private Vector3f position;
    private int hp;

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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
