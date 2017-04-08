package engine.network;

/**
 * Created by stasbar on 08.04.2017.
 */
public class WrongOpCodeException extends RuntimeException{

    public WrongOpCodeException(int opCodeExpected,int opCodeReceived) {
        super(String.format("Received wrong opcode, expected: %d but received %d ", opCodeExpected,opCodeReceived));
    }
}
