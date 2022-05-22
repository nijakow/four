package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.Fiber;

public class LoadGlobalInstruction extends VMInstruction {
    private final STSymbol symbol;

    public LoadGlobalInstruction(STSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.setAccu(fiber.getVM().getWorld().getValue(this.symbol));
        return getNext();
    }
}
