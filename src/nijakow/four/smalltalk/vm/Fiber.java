package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.SmalltalkVM;
import nijakow.four.smalltalk.objects.STClosure;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STMethod;

import java.util.Vector;

public class Fiber {
    private final SmalltalkVM vm;
    private boolean isPaused = false;
    private STInstance accu;
    private Context top;
    private final Vector<STInstance> stack = new Vector<>();
    private int sp = 0;

    public Fiber(SmalltalkVM vm) {
        this.vm = vm;
    }

    public Context top() {
        return null;
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

    public void loadClosure(STMethod method) {
        setAccu(new STClosure(method, top()));
    }

    public void push() {
        stack.set(sp++, getAccu());
    }

    public void normalReturn() {
        sp = top().getBase();
        top = top().getParent();
    }

    public void lexicalReturn() {
        Context context = top().getLexical();
        sp = top().getBase();
        while (context != null) {
            if (top() == context) {
                sp = top().getBase();
                top = top().getParent();
                context = context.getLexical();
            }
        }
        top = top().getParent();
    }

    public void runForAWhile() {
        for (int x = 0; x < 32 * 1024; x++) {
            if (!isRunning()) break;
            top().step(this);
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
}
