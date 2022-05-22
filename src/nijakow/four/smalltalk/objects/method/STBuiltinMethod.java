package nijakow.four.smalltalk.objects.method;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.STClass;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.vm.Fiber;

import java.util.function.BiConsumer;

public class STBuiltinMethod extends STInstance implements STMethod {
    private final BiConsumer<Fiber, STInstance[]> consumer;

    public STBuiltinMethod(BiConsumer<Fiber, STInstance[]> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void execute(Fiber fiber, int args) {
        args++; // Also send the receiver
        STInstance[] arglist = new STInstance[args];
        while (args --> 0)
            arglist[args] = fiber.pop();
        consumer.accept(fiber, arglist);
    }

    @Override
    public STClass getClass(World world) {
        return world.getBuiltinMethodClass();
    }
}
