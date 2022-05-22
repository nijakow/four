package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class NOOPInstruction extends VMInstruction {
    @Override
    public VMInstruction run(Fiber fiber) {
        return getNext();
    }
}
