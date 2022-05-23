package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.objects.STInstance;

public interface Builtin {
    void accept(Fiber fiber, STInstance[] args) throws FourException;
}
