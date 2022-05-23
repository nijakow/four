package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.vm.Context;
import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;

public class STClosure extends STInstance {
    private final STCompiledMethod method;
    private final Context context;

    public STClosure(STCompiledMethod method, Context context) {
        this.method = method;
        this.context = context;
    }

    @Override
    public STClosure asClosure() throws FourException {
        return this;
    }

    @Override
    public STClass getClass(World world) {
        return world.getClosureClass();
    }

    public void execute(Fiber fiber, int args) {
        this.method.execute(fiber, args, context);
    }

    public Context getContext() {
        return context;
    }
}
