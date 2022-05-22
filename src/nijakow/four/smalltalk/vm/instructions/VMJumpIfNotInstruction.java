package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STBoolean;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.vm.Fiber;

public class VMJumpIfNotInstruction extends VMInstruction {
    private VMInstruction target;

    public VMJumpIfNotInstruction() {
    }

    public void setTarget(VMInstruction value) {
        this.target = target;
    }

    @Override
    public void run(Fiber fiber) {
        if (!fiber.getAccu().isTrue())
            fiber.top().jumpTo(target);
    }
}
