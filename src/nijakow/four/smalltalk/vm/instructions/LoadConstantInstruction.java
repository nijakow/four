package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.vm.Fiber;

public class LoadConstantInstruction extends VMInstruction {
    private final STInstance value;

    public LoadConstantInstruction(STInstance value) {
        this.value = value;
    }

    @Override
    public void run(Fiber fiber) {
        fiber.setAccu(this.value);
    }
}
