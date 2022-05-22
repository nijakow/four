package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class VMJumpInstruction extends VMInstruction {
    private VMInstruction target;

    public VMJumpInstruction() {
    }

    public void setTarget(VMInstruction value) {
        this.target = target;
    }

    @Override
    public void run(Fiber fiber) {
        fiber.top().jumpTo(target);
    }
}
