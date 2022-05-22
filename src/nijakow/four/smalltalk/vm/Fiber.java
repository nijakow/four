package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.objects.STClosure;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STMethod;

import java.util.Vector;

public class Fiber {
    private STInstance accu;
    private Context top;
    private final Vector<STInstance> stack = new Vector<>();
    private int sp = 0;

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
}
