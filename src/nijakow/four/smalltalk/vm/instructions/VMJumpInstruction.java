package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class VMJumpInstruction extends VMInstruction {
    private VMInstruction target;

    public VMJumpInstruction() {
    }

    public void setTarget(VMInstruction value) {
        this.target = value;
    }

    @Override
    public String toString() {
        return "JUMP -> " + target;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        return target;
    }
}
