package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.Fiber;

public class LoadGlobalInstruction extends VMInstruction {
    private final STSymbol symbol;

    public LoadGlobalInstruction(STSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "LOAD_GLOBAL " + symbol;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.loadGlobal(this.symbol);
        return getNext();
    }
}
