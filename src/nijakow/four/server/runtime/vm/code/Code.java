package nijakow.four.server.runtime.vm.code;

import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.vm.fiber.Fiber;

public interface Code {
    ByteCode asByteCode();
	void invoke(Fiber fiber, int args, Instance self) throws FourRuntimeException;
}
