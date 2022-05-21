package nijakow.four.smalltalk.compiler;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STMethod;
import nijakow.four.smalltalk.objects.STObject;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.instructions.*;

import java.util.ArrayList;
import java.util.List;

public class Compiler {
    private final Compiler parent;
    private final List<STSymbol> locals = new ArrayList<>();
    private VMInstruction first;
    private VMInstruction last;

    private Compiler(Compiler parent) {
        this.parent = parent;
    }

    public Compiler() {
        this(null);
    }

    public void addLocal(STSymbol symbol) { locals.add(symbol); }

    private void addInstruction(VMInstruction instruction) {
        if (first == null) {
            first = instruction;
            last = first;
        } else {
            last.setNext(instruction);
            last = instruction;
        }
    }

    public void writeLoadSelf() {
        addInstruction(new LoadSelfInstruction());
    }

    public void writeLoadConstant(STInstance value) {
        addInstruction(new LoadConstantInstruction(value));
    }

    public void writeLoadClosure(STMethod method) {
        addInstruction(new LoadClosureInstruction(method));
    }

    public void writeLoadLocal(int depth, int offset) {
        addInstruction(new LoadLocalInstruction(depth, offset));
    }

    public void writeStoreLocal(int depth, int offset) {
        addInstruction(new StoreLocalInstruction(depth, offset));
    }

    public void writeLoadGlobal(STSymbol symbol) {
        addInstruction(new LoadGlobalInstruction(symbol));
    }

    public void writeStoreGlobal(STSymbol symbol) {
        addInstruction(new StoreGlobalInstruction(symbol));
    }

    public void writePush() {
        addInstruction(new PushInstruction());
    }
}
