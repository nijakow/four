package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;

public class STArray extends STInstance {
    private final STInstance[] values;

    public STArray(STInstance[] values) {
        this.values = values;
    }

    public STArray(int size) {
        this.values = new STInstance[size];
        for (int x = 0; x < size; x++)
            this.values[x] = STNil.get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#(");
        for (int x = 0; x < values.length; x++) {
            if (x > 0) sb.append(" ");
            sb.append(values[x].toString());
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public STClass getClass(World world) {
        return world.getArrayClass();
    }

    public int getSize() {
        return values.length;
    }

    public STInstance get(int index) {
        if (index >= 0 && index < values.length)
            return values[index];
        return STNil.get();
    }

    public void set(int index, STInstance value) {
        if (index >= 0 && index < values.length)
            values[index] = value;
    }
}
