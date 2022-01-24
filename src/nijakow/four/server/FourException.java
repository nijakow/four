package nijakow.four.server;

public class FourException extends Exception {
    public FourException() { this(""); }

    public FourException(String message) {
        super(message);
    }
}
