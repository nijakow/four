package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.vm.instructions.VMInstruction;

public class STMethod extends STInstance {
    private final VMInstruction instructions;
    private final int args;
    private final int locals;
    private final String source;

    public STMethod(int args, int locals, VMInstruction first, String source) {
        this.instructions = first;
        this.args = args;
        this.locals = locals;
        this.source = source;
    }

    public STMethod(int args, int locals, VMInstruction first) {
        this(args, locals, first, null);
    }
}
