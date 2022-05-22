package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class VMJumpIfNotInstruction extends VMInstruction {
    private VMInstruction target;

    public VMJumpIfNotInstruction() {
    }

    public void setTarget(VMInstruction value) {
        this.target = target;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        if (!fiber.getAccu().isTrue())
            return target;
        return getNext();
    }
}
