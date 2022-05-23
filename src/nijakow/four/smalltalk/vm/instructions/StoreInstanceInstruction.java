package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STObject;
import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;

public class StoreInstanceInstruction extends VMInstruction {
    private final int offset;

    public StoreInstanceInstruction(int offset) {
        this.offset = offset;
    }

    @Override
    public VMInstruction run(Fiber fiber) throws FourException {
        STInstance value = fiber.getAccu();
        fiber.loadSelf();
        fiber.getAccu().asObject().set(offset, value);
        return getNext();
    }
}
