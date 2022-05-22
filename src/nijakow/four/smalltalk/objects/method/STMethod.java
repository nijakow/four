package nijakow.four.smalltalk.objects.method;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.vm.Context;
import nijakow.four.smalltalk.vm.Fiber;

public interface STMethod {
    void execute(Fiber fiber, int args, Context context);
    STInstance asInstance();
}
