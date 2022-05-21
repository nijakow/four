package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.STMethod;
import nijakow.four.smalltalk.vm.Fiber;

public class LoadClosureInstruction extends VMInstruction {
    private final STMethod method;

    public LoadClosureInstruction(STMethod method) {
        this.method = method;
    }

    @Override
    public void run(Fiber fiber) {
        fiber.loadClosure(method);
    }
}
