package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public interface Code {
	void invoke(Fiber fiber, int args, Blue self);
}
