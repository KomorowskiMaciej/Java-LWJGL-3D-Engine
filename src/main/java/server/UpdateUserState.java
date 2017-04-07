package server;

/**
 * Created by stasbar on 24.03.2017.
 */
public class UpdateUserState implements Runnable {
    private UserState newUserState;
    private UserServerState userServerState;

    UpdateUserState(UserServerState userServerState, UserState newUserState) {
        this.userServerState = userServerState;
        this.newUserState = newUserState;
    }

    @Override
    public void run() {
        userServerState.updateUserState(newUserState);
    }

}
