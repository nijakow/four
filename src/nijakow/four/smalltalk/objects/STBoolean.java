package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;

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
    public STClass getClass(World world) {
        return world.getBooleanClass();
    }
}
