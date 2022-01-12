package nijakow.four;

public class FourException extends Exception {
    public FourException() { this(""); }

    public FourException(String message) {
        super(message);
    }
}
