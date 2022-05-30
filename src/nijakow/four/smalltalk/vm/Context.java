package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.objects.STClosure;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.vm.instructions.VMInstruction;

public class Context {
    private final Context parent;
    private final Context lexical;
    private final STCompiledMethod method;
    private final int base;
    private VMInstruction instruction;
    private STClosure handler;

    public Context(Context parent, Context lexical, STCompiledMethod method, VMInstruction instruction, int base) {
        this.parent = parent;
        this.lexical = lexical;
        this.method = method;
        this.instruction = instruction;
        this.base = base;
    }

    public Context getParent() {
        return parent;
    }

    public Context getLexical() {
        return lexical;
    }

    public int getBase() {
        return this.base;
    }

    public STClosure getHandler() {
        return handler;
    }

    public void setHandler(STClosure handler) {
        this.handler = handler;
    }

    public void step(Fiber fiber) throws FourException {
        if (instruction == null)
            fiber.normalReturn();
        else {
            instruction = instruction.run(fiber);
        }
    }

    public void jumpTo(VMInstruction target) {
        instruction = target;
    }
}
