package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.objects.STClass;
import nijakow.four.smalltalk.objects.STClosure;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.util.Pair;
import nijakow.four.smalltalk.vm.instructions.VMInstruction;

public class Context {
    private final Context parent;
    private final Context lexical;
    private final STCompiledMethod method;
    private final int base;
    private final int args;
    private VMInstruction instruction;
    private Pair<STClass, STClosure> handler;

    public Context(Context parent, Context lexical, STCompiledMethod method, VMInstruction instruction, int base, int args) {
        this.parent = parent;
        this.lexical = lexical;
        this.method = method;
        this.instruction = instruction;
        this.base = base;
        this.args = args;
    }

    public Context getParent() {
        return parent;
    }

    public Context getLexical() {
        return lexical;
    }

    public STCompiledMethod getMethod() { return this.method; }

    public int getBase() { return this.base; }

    public int getArgs() { return this.args; }

    public Pair<STClass, STClosure> getHandler() {
        return handler;
    }

    public void setHandler(STClass clazz, STClosure handler) {
        this.handler = new Pair<>(clazz, handler);
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
