package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;

public class LoadInstanceInstruction extends VMInstruction {
    private final int offset;

    public LoadInstanceInstruction(int offset) {
        this.offset = offset;
    }

    @Override
    public VMInstruction run(Fiber fiber) throws FourException {
        fiber.setAccu(fiber.getSelf().asObject().get(offset));
        return getNext();
    }
}
