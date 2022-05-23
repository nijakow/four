package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.vm.FourException;

public class STNil extends STInstance {
    private static final STNil NIL = new STNil();

    public static STNil get() { return NIL; }
    public static STInstance wrap(STInstance instance) { return (instance == null) ? get() : instance; }

    private STNil() {}

    @Override
    public String toString() {
        return "nil";
    }

    @Override
    public STNil asNil() throws FourException {
        return this;
    }

    @Override
    public STClass getClass(World world) {
        return world.getNilClass();
    }
}
