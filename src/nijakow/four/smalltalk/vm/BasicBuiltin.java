package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;

public interface BasicBuiltin {
    void accept(Fiber fiber) throws FourException;
}
