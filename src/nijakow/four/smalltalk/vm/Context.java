package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.vm.instructions.VMInstruction;

public class Context {
    private final Context parent;
    private final Context lexical;
    private final int base;
    private VMInstruction instruction;

    public Context(Context parent, Context lexical, int base) {
        this.parent = parent;
        this.lexical = lexical;
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

    public void step(Fiber fiber) {
        instruction.run(fiber);
        instruction = instruction.getNext();
    }
}
