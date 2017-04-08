package engine.network;

/**
 * Created by stasbar on 08.04.2017.
 */
public class FailedLoginException extends RuntimeException {
    public FailedLoginException(String message) {
        super(message);
    }
}
