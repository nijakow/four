package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.method.STMethod;

public abstract class STInstance {
    public abstract STClass getClass(World world);
    public STMethod getInstanceMethod(World world, STSymbol name) {
        return getClass(world).findMethod(name);
    }
}
