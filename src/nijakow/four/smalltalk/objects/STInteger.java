package nijakow.four.smalltalk.objects;

public class STInteger extends STInstance {
    private final int value;

    public STInteger(int value) {
        this.value = value;
    }

    public static STInteger get(int value) {
        return new STInteger(value);
    }
}
