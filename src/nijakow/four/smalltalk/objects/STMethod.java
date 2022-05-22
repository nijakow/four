package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.vm.instructions.VMInstruction;

public class STMethod extends STInstance {
    private final VMInstruction instructions;
    private final int args;
    private final int locals;

    public STMethod(int args, int locals, VMInstruction first) {
        this.instructions = first;
        this.args = args;
        this.locals = locals;
    }
}
