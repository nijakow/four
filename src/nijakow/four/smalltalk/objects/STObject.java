package nijakow.four.smalltalk.objects;

public class STObject extends STInstance {
    private STClass clazz;
    private STInstance[] values;

    public STInstance get(int index) {
        return values[index];
    }

    public void set(int index, STInstance value) {
        values[index] = value;
    }
}
