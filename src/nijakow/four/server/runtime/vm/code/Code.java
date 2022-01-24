package nijakow.four.server.runtime.vm.code;

import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.vm.Fiber;

public interface Code {
	void invoke(Fiber fiber, int args, Instance self) throws FourRuntimeException;
}
