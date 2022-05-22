package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.vm.Context;

public class STClosure extends STInstance {
    private final STCompiledMethod method;
    private final Context context;

    public STClosure(STCompiledMethod method, Context context) {
        this.method = method;
        this.context = context;
    }

    @Override
    public STClass getClass(World world) {
        return world.getClosureClass();
    }
}
