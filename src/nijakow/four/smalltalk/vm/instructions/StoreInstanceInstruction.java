package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STObject;
import nijakow.four.smalltalk.vm.Fiber;

public class StoreInstanceInstruction extends VMInstruction {
    private final int offset;

    public StoreInstanceInstruction(int offset) {
        this.offset = offset;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        STInstance value = fiber.getAccu();
        fiber.loadSelf();
        ((STObject) fiber.getAccu()).set(offset, value);
        return getNext();
    }
}
