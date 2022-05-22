package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.Fiber;

public class StoreGlobalInstruction extends VMInstruction {
    private final STSymbol symbol;

    public StoreGlobalInstruction(STSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public void run(Fiber fiber) {
        fiber.getVM().getWorld().setValue(this.symbol, fiber.getAccu());
    }
}
