package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.Fiber;

public class SendInstruction extends VMInstruction {
    private final STSymbol message;
    private final int args;

    public SendInstruction(STSymbol message, int args) {
        this.message = message;
        this.args = args;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.send(this.message, this.args);
        return getNext();
    }
}
