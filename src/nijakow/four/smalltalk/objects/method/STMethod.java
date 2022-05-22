package nijakow.four.smalltalk.objects.method;

import nijakow.four.smalltalk.vm.Fiber;

public interface STMethod {
    void execute(Fiber fiber, int args);
}
