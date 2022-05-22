package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;

public class STObject extends STInstance {
    private STClass clazz;
    private STInstance[] values;

    STObject(STClass clazz, int slotCount) {
        this.clazz = clazz;
        this.values = new STInstance[slotCount];
    }

    public STInstance get(int index) {
        return values[index];
    }

    public void set(int index, STInstance value) {
        values[index] = value;
    }

    @Override
    public STClass getClass(World world) {
        return this.clazz;
    }
}
