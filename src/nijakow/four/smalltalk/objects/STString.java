package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;

public class STString extends STInstance {
    private final String value;

    public STString(String value) {
        this.value = value;
    }

    @Override
    public STClass getClass(World world) {
        return world.getStringClass();
    }
}
