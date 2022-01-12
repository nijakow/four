package nijakow.four.runtime;

import nijakow.four.runtime.vm.Fiber;

public interface Code {
	void invoke(Fiber fiber, int args, Instance self);
}
