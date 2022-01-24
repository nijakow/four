package nijakow.four.runtime.vm.code;

import nijakow.four.runtime.exceptions.FourRuntimeException;
import nijakow.four.runtime.objects.Instance;
import nijakow.four.runtime.vm.Fiber;

public interface Code {
	void invoke(Fiber fiber, int args, Instance self) throws FourRuntimeException;
}
