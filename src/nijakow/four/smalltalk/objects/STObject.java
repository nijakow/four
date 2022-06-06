package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.vm.FourException;

public class STObject extends STInstance {
    private STClass clazz;
    private STInstance[] values;

    STObject(STClass clazz, int slotCount) {
        this.clazz = clazz;
        this.values = new STInstance[slotCount];
        for (int x = 0; x < this.values.length; x++)
            this.values[x] = STNil.get();
    }

    @Override
    public STObject asObject() throws FourException {
        return this;
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
