package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class LoadSelfInstruction extends VMInstruction {

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.loadSelf();
        return getNext();
    }
}
