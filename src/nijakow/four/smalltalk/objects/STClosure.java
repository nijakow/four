package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.vm.Context;

public class STClosure extends STInstance {
    private final STMethod method;
    private final Context context;

    public STClosure(STMethod method, Context context) {
        this.method = method;
        this.context = context;
    }
}
