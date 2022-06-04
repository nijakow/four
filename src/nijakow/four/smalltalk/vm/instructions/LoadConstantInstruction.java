package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.vm.Fiber;

public class LoadConstantInstruction extends VMInstruction {
    private final STInstance value;

    public LoadConstantInstruction(STInstance value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LOAD_CONSTANT " + value;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.setAccu(this.value);
        return getNext();
    }
}
