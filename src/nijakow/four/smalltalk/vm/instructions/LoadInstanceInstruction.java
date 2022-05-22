package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STObject;
import nijakow.four.smalltalk.vm.Fiber;

public class LoadInstanceInstruction extends VMInstruction {
    private final int offset;

    public LoadInstanceInstruction(int offset) {
        this.offset = offset;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.setAccu(((STObject) fiber.getAccu()).get(offset));
        return getNext();
    }
}
