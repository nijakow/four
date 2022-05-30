package nijakow.four.smalltalk.objects.method;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.STClass;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.Context;
import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;
import nijakow.four.smalltalk.vm.instructions.VMInstruction;

public class STCompiledMethod extends STInstance implements STMethod {
    private final STClass clazz;
    private final STSymbol name;
    private final VMInstruction instructions;
    private final int args;
    private final int locals;
    private final String source;

    public STCompiledMethod(STClass clazz, STSymbol name, int args, int locals, VMInstruction first, String source) {
        this.clazz = clazz;
        this.name = name;
        this.instructions = first;
        this.args = args;
        this.locals = locals;
        this.source = source;
    }

    public STCompiledMethod(STClass clazz, STSymbol name, int args, int locals, VMInstruction first) {
        this(clazz, name, args, locals, first, null);
    }

    public STCompiledMethod(int args, int locals, VMInstruction first, String source) {
        this(null, null, args, locals, first, source);
    }

    public STCompiledMethod(int args, int locals, VMInstruction first) {
        this(null, null, args, locals, first, null);
    }

    @Override
    public String toString() {
        return "CompiledMethod";
    }

    @Override
    public STCompiledMethod asCompiledMethod() throws FourException {
        return this;
    }

    @Override
    public void execute(Fiber fiber, int args, Context context) {
        // TODO: Check args!
        fiber.enter(context, this, this.instructions, args, this.locals);
    }

    @Override
    public STClass getClass(World world) {
        return world.getCompiledMethodClass();
    }

    @Override
    public STInstance asInstance() {
        return this;
    }

    public STSymbol getName() { return this.name; }

    public STClass getHoldingClass() { return this.clazz; }

    public String getSource() {
        return this.source == null ? "" : this.source;
    }
}
