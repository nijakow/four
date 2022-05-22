package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;

public class STInteger extends STInstance {
    private final int value;

    public STInteger(int value) {
        this.value = value;
    }

    public static STInteger get(int value) {
        return new STInteger(value);
    }

    @Override
    public STClass getClass(World world) {
        return world.getIntegerClass();
    }
}
