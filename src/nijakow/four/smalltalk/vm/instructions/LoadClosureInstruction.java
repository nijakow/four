package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.vm.Fiber;

public class LoadClosureInstruction extends VMInstruction {
    private final STCompiledMethod method;

    public LoadClosureInstruction(STCompiledMethod method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "LOAD_CLOSURE";
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.loadClosure(method);
        return getNext();
    }
}
