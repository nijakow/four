package nijakow.four.smalltalk.compiler;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STMethod;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.instructions.*;

import java.util.ArrayList;
import java.util.List;

public class STCompiler {
    private final STCompiler parent;
    private final List<STSymbol> locals = new ArrayList<>();
    private int argCount = 0;
    private VMInstruction first;
    private VMInstruction last;

    private STCompiler(STCompiler parent) {
        this.parent = parent;
    }

    public STCompiler() {
        this(null);
    }

    public STCompiler subscope() {
        return new STCompiler(this);
    }

    public void addLocal(STSymbol symbol) { locals.add(symbol); }
    public void addArg(STSymbol arg) { argCount++; addLocal(arg); }

    public STMethod finish() {
        return new STMethod(argCount, locals.size(), first);
    }

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

    public void writeSend(STSymbol message, int args) {
        addInstruction(new SendInstruction(message, args));
    }

    public void writeReturn() {
        addInstruction(new ReturnInstruction());
    }

    public void writeLoad(STSymbol symbol) {

    }
}
