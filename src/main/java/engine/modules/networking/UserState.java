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

        return userID.equals(userState.userID);
    }

    @Override
    public int hashCode() {
        return userID.hashCode();
    }
}
