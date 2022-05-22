package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class ReturnInstruction extends VMInstruction {
    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.lexicalReturn();
        return getNext();
    }
}
