package nijakow.four.smalltalk.vm;

import nijakow.four.server.Four;
import nijakow.four.smalltalk.SmalltalkVM;
import nijakow.four.smalltalk.objects.*;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.vm.instructions.VMInstruction;

import java.util.*;
import java.util.function.Consumer;

public class Fiber {
    private final SmalltalkVM vm;
    private final Map<STSymbol, STInstance> fiberLocals = new HashMap<>();
    private final List<Consumer<STInstance>> terminationCallbacks = new ArrayList<>();
    private boolean isPaused = true;
    private STInstance accu;
    private Context top;
    private final Vector<STInstance> stack;
    private int sp = 0;

    public Fiber(SmalltalkVM vm) {
        this.vm = vm;
        this.stack = new Vector<>(1024 * 32);
        this.stack.setSize(1024 * 32);
    }

    public SmalltalkVM getVM() {
        return this.vm;
    }

    public void onExit(Consumer<STInstance> callback) { this.terminationCallbacks.add(callback); }

    public Context top() {
        return top;
    }

    public STInstance getAccu() {
        return this.accu;
    }

    public void setAccu(STInstance value) {
        this.accu = value;
    }

    public STInstance getSelf() {
        return stack.get(top().getBase());
    }

    public void loadSelf() {
        setAccu(getSelf());
    }

    public void loadLexicalSelf(Context context) {
        if (context == null)
            loadSelf();
        else {
            while (context.getLexical() != null)
                context = context.getLexical();
            setAccu(stack.get(context.getBase()));
        }
    }

    private Context lexical(int depth) {
        Context context = top();
        while (depth --> 0)
            context = context.getLexical();
        return context;
    }

    public void loadVariable(int depth, int offset) {
        setAccu(stack.get(lexical(depth).getBase() + offset + 1));
    }

    public void storeVariable(int depth, int offset) {
        stack.set(lexical(depth).getBase() + offset + 1, getAccu());
    }

    public void loadGlobal(STSymbol global) {
        if (getVM().getWorld().isSpecial(global))
            setAccu(fiberLocals.getOrDefault(global, STNil.get()));
        else
            setAccu(getVM().getWorld().getValue(global));
    }

    public void storeGlobal(STSymbol global) {
        if (getVM().getWorld().isSpecial(global))
            fiberLocals.put(global, getAccu());
        else
            getVM().getWorld().setValue(global, getAccu());
    }

    public void loadClosure(STCompiledMethod method) {
        setAccu(new STClosure(method, top()));
    }

    public void push() {
        push(getAccu());
    }

    public void push(STInstance value) {
        stack.set(sp++, value);
    }

    public STInstance pop() {
        return stack.get(--sp);
    }

    public void enter(Context lexical, STCompiledMethod method, VMInstruction instruction, int args, int locals) {
        Context context = new Context(top(), lexical, method, instruction, sp - args - 1);
        sp += locals - args;
        this.top = context;
    }

    public void enter(STInstance self, STSymbol message, STInstance[] args) throws FourException {
        push(self);
        for (STInstance arg : args)
            push(arg);
        setAccu(self);
        send(message, args.length);
    }
    public void enter(STInstance self, String message, STInstance[] args) throws FourException {
        enter(self, STSymbol.get(message), args);
    }

    public void enterSuper(STInstance self, STSymbol message, STInstance[] args) throws FourException {
        push(self);
        for (STInstance arg : args)
            push(arg);
        setAccu(self);
        superSend(message, args.length);
    }

    public void send(STSymbol message, int args) throws FourException {
        STInstance instance = stack.get(sp - args - 1);
        if (instance.isForeign()) {
            STInstance[] arguments = new STInstance[args];
            while (args --> 0)
                arguments[args] = pop();
            pop();  // Pop the instance itself
            pause();
            instance.asForeign().send(message, arguments, (result) -> restartWithValue(result));
        } else {
            STMethod m = instance.getInstanceMethod(this.getVM().getWorld(), message);
            if (m == null)
                throw new FourException("Method not found: " + message + "!");
            m.execute(this, args, null);
        }
    }

    public void superSend(STSymbol message, int args) throws FourException {
        STInstance instance = stack.get(sp - args - 1);
        if (instance.isForeign()) {
            STInstance[] arguments = new STInstance[args];
            while (args --> 0)
                arguments[args] = pop();
            pop();  // Pop the instance itself
            pause();
            instance.asForeign().sendSuper(message, arguments, (result) -> restartWithValue(result));
        } else {
            STMethod m = instance.getInstanceSuperMethod(this.getVM().getWorld(), message);
            if (m == null)
                throw new FourException("Method not found: " + message + "!");
            m.execute(this, args, null);
        }
    }

    private void popContext() {
        sp = top().getBase();
        top = top().getParent();
    }

    public void normalReturn() {
        popContext();
        maybeHalt();
    }

    public void lexicalReturn() {
        Context context = top().getLexical();
        sp = top().getBase();
        while (context != null) {
            popContext();
            if (top() == context)
                context = context.getLexical();
        }
        top = top().getParent();
        maybeHalt();
    }

    public boolean callHandler(STInstance value) {
        while (top() != null) {
            Context context = top();
            loadSelf();
            popContext();
            if (context.getHandler() != null && value.getClass(this.getVM().getWorld()).isSubclassOf(context.getHandler().getFirst())) {
                push();
                setAccu(value);
                push();
                context.getHandler().getSecond().execute(this, 1);
                return true;
            }
        }
        return false;
    }

    public void runForAWhile() throws FourException {
        for (int x = 0; x < 32 * 1024; x++) {
            maybeHalt();
            if (!isRunning()) break;
            try {
                top().step(this);
            } catch (FourException e) {
                if (!callHandler(STNil.get()))
                    throw e;
            }
        }
    }

    public boolean isRunning() {
        return !isPaused() && !isDead();
    }
    public boolean isPaused() { return isPaused; }
    public boolean isDead() { return top() == null; }

    public void restart() {
        if (isPaused) {
            isPaused = false;
            vm.restartFiber(this);
        }
    }

    public void restartWithValue(STInstance value) {
        setAccu(value);
        restart();
    }

    public void pause() {
        if (!isPaused) {
            isPaused = true;
            vm.pauseFiber(this);
        }
    }

    public void forceUnpause() {
        isPaused = false;
    }

    public void halt() {
        /*
         * TODO: Put a lock bit into the system that blocks normal reactivations!
         *                                                           - nijakow
         */
        pause();
        for (Consumer<STInstance> callback : terminationCallbacks)
            callback.accept(getAccu());
    }

    public void maybeHalt() {
        if (isDead() && !isPaused()) {
            halt();
        }
    }

    public void throwValue(STInstance value) throws FourException {
        if (!callHandler(value)) {
            setAccu(value);
            halt();
            throw new FourException("Uncaught thrown value: " + value);
        }
    }
}
