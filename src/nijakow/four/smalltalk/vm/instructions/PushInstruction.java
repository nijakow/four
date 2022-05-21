package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class PushInstruction extends VMInstruction {
    @Override
    public void run(Fiber fiber) {
        fiber.push();
    }
}
