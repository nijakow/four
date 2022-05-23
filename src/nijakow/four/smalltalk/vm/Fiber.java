package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.SmalltalkVM;
import nijakow.four.smalltalk.objects.STClosure;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STString;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.vm.instructions.VMInstruction;

import java.util.Vector;

public class Fiber {
    private final SmalltalkVM vm;
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

    public Context top() {
        return top;
    }

    public STInstance getAccu() {
        return this.accu;
    }

    public void setAccu(STInstance value) {
        this.accu = value;
    }

    public void loadSelf() {
        setAccu(stack.get(top().getBase()));
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

    public void enter(Context lexical, VMInstruction instruction, int args, int locals) {
        Context context = new Context(top(), lexical, instruction, sp - args - 1);
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

    public void send(STSymbol message, int args) throws FourException {
        STInstance instance = stack.get(sp - args - 1);
        STMethod m = instance.getInstanceMethod(this.getVM().getWorld(), message);
        if (m == null)
            throw new FourException("Method not found: " + message + "!");
        m.execute(this, args, null);
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

    public boolean callHandler() {
        while (top() != null) {
            Context context = top();
            loadSelf();
            popContext();
            if (context.getHandler() != null) {
                push();
                context.getHandler().execute(this, 0);
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
                if (!callHandler())
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

    public void halt() {
        /*
         * TODO: Put a lock bit into the system that blocks normal reactivations!
         *                                                           - nijakow
         */
        pause();
    }

    public void maybeHalt() {
        if (isDead() && !isPaused()) {
            halt();
        }
    }
}
