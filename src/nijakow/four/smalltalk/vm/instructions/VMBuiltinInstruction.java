package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;

import java.util.function.Consumer;

public class VMBuiltinInstruction extends VMInstruction {
    private final STSymbol builtin;

    public VMBuiltinInstruction(STSymbol builtin) {
        this.builtin = builtin;
    }

    @Override
    public VMInstruction run(Fiber fiber) throws FourException {
        Consumer<Fiber> b = builtin.getBuiltin(fiber.getVM().getWorld());
        if (b == null)
            throw new FourException("Builtin not found: " + builtin);
        b.accept(fiber);
        return getNext();
    }
}
