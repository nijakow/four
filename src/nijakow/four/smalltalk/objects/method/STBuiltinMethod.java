package nijakow.four.smalltalk.objects.method;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.STClass;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.vm.Builtin;
import nijakow.four.smalltalk.vm.Context;
import nijakow.four.smalltalk.vm.Fiber;

import java.util.function.BiConsumer;

public class STBuiltinMethod extends STInstance implements STMethod {
    private final Builtin consumer;

    public STBuiltinMethod(Builtin consumer) {
        this.consumer = consumer;
    }

    @Override
    public void execute(Fiber fiber, int args, Context context) {
        args++; // Also send the receiver
        STInstance[] arglist = new STInstance[args];
        while (args --> 0)
            arglist[args] = fiber.pop();
        fiber.setAccu(arglist[0]);
        consumer.accept(fiber, arglist);
    }

    @Override
    public STInstance asInstance() {
        return this;
    }

    @Override
    public STClass getClass(World world) {
        return world.getBuiltinMethodClass();
    }
}
