package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class PushInstruction extends VMInstruction {

    @Override
    public String toString() {
        return "PUSH";
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.push();
        return getNext();
    }
}
