package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.vm.FourException;

public class STInteger extends STInstance {
    private final int value;

    public STInteger(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public STInteger asInteger() throws FourException {
        return this;
    }

    @Override
    public boolean is(Object obj) {
        return (obj instanceof STInteger) && getValue() == ((STInteger) obj).getValue();
    }

    @Override
    public STClass getClass(World world) {
        return world.getIntegerClass();
    }

    public int getValue() {
        return this.value;
    }

    public static STInteger get(int value) {
        return new STInteger(value);
    }
}
