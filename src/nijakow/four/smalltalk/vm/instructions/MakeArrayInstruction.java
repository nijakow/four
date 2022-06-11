package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STArray;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;

public class MakeArrayInstruction extends VMInstruction {
    private final int length;

    public MakeArrayInstruction(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "MAKE_ARRAY " + length;
    }

    @Override
    public VMInstruction run(Fiber fiber) throws FourException {
        STInstance[] instances = new STInstance[length];
        for (int i = length - 1; i >= 0; i--)
            instances[i] = fiber.pop();
        fiber.setAccu(new STArray(instances));
        return getNext();
    }
}
