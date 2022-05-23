package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.vm.FourException;

public class STBoolean extends STInstance {
    private static final STBoolean TRUE = new STBoolean();
    private static final STBoolean FALSE = new STBoolean();

    public static STBoolean getTrue() { return TRUE; }
    public static STBoolean getFalse() { return FALSE; }

    private STBoolean() {}

    public static STInstance get(boolean value) {
        if (value) return TRUE;
        else       return FALSE;
    }

    @Override
    public String toString() {
        return isTrue() ? "true" : "false";
    }

    @Override
    public STBoolean asBoolean() throws FourException {
        return this;
    }

    @Override
    public STClass getClass(World world) {
        return world.getBooleanClass();
    }
}
