package nijakow.four.smalltalk.compiler;

import nijakow.four.share.util.Pair;
import nijakow.four.smalltalk.objects.STClass;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.instructions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class STCompiler {
    private final STClass clazz;
    private final STCompiler parent;
    private final List<STSymbol> locals = new ArrayList<>();
    private int argCount = 0;
    private VMInstruction first;
    private VMInstruction last;

    public STCompiler(STClass clazz, STCompiler parent) { this.clazz = clazz; this.parent = parent; }

    public STCompiler subscope() {
        return new STCompiler(this.clazz, this);
    }

    public void addLocal(STSymbol symbol) { locals.add(symbol); }
    public void addArg(STSymbol arg) { argCount++; addLocal(arg); }

    public STCompiledMethod finish(String source) {
        return new STCompiledMethod(argCount, locals.size(), first, source);
    }
    public STCompiledMethod finish() {
        return new STCompiledMethod(argCount, locals.size(), first);
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

    VMInstruction currentInstruction() {
        if (last == null)
            addInstruction(new NOOPInstruction());
        return last;
    }

    public void writeLoadSelf() {
        addInstruction(new LoadSelfInstruction());
    }

    public void writeLoadConstant(STInstance value) {
        addInstruction(new LoadConstantInstruction(value));
    }

    public void writeLoadClosure(STCompiledMethod method) {
        addInstruction(new LoadClosureInstruction(method));
    }

    public void writeLoadLocal(int depth, int offset) {
        addInstruction(new LoadLocalInstruction(depth, offset));
    }

    public void writeStoreLocal(int depth, int offset) {
        addInstruction(new StoreLocalInstruction(depth, offset));
    }

    public void writeLoadInstance(int offset) {
        addInstruction(new LoadInstanceInstruction(offset));
    }
    public void writeStoreInstance(int offset) {
        addInstruction(new StoreInstanceInstruction(offset));
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

    private Pair<Integer, Integer> find(STSymbol symbol) {
        int index = locals.indexOf(symbol);
        if (index >= 0)
            return new Pair<>(0, index);
        Pair<Integer, Integer> result = (parent == null) ? null : parent.find(symbol);
        if (result == null)
            return null;
        else
            return new Pair<>(result.getFirst() + 1, result.getSecond());
    }

    public void writeLoad(STSymbol symbol) {
        int index = (this.clazz == null) ? -1 : this.clazz.findMember(symbol);
        if (index >= 0)
            writeLoadInstance(index);
        else {
            Pair<Integer, Integer> result = find(symbol);
            if (result != null)
                writeLoadLocal(result.getFirst(), result.getSecond());
            else
                writeLoadGlobal(symbol);
        }
    }

    public void writeStore(STSymbol symbol) {
        int index = (this.clazz == null) ? -1 : this.clazz.findMember(symbol);
        if (index >= 0)
            writeStoreInstance(index);
        else {
            Pair<Integer, Integer> result = find(symbol);
            if (result != null)
                writeStoreLocal(result.getFirst(), result.getSecond());
            else
                writeStoreGlobal(symbol);
        }
    }

    Consumer<VMInstruction> writeJump() {
        VMJumpInstruction jump = new VMJumpInstruction();
        addInstruction(jump);
        return (value) -> jump.setTarget(value);
    }

    Consumer<VMInstruction> writeJumpIfNot() {
        VMJumpIfNotInstruction jump = new VMJumpIfNotInstruction();
        addInstruction(jump);
        return (value) -> jump.setTarget(value);
    }
}
