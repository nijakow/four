package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STMethod;

public class Fiber {
    private STInstance accu;
    private Context top;

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
        setAccu(top().getSelf());
    }

    public void loadVariable(int depth, int offset) {
        // TODO
    }

    public void storeVariable(int depth, int offset) {
        // TODO
    }

    public void loadClosure(STMethod method) {
        // TODO
    }

    public void push() {
        // TODO
    }
}
