package nijakow.four.smalltalk.objects.method;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.STClass;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.instructions.VMInstruction;

public class STCompiledMethod extends STInstance implements STMethod {
    private final VMInstruction instructions;
    private final int args;
    private final int locals;
    private final String source;

    public STCompiledMethod(int args, int locals, VMInstruction first, String source) {
        this.instructions = first;
        this.args = args;
        this.locals = locals;
        this.source = source;
    }

    public STCompiledMethod(int args, int locals, VMInstruction first) {
        this(args, locals, first, null);
    }

    @Override
    public void execute(Fiber fiber, int args) {
        // TODO: Check args!
        fiber.enter(null, this.instructions, args);
    }

    @Override
    public STClass getClass(World world) {
        return world.getCompiledMethodClass();
    }
}
