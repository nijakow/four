package nijakow.four.server.runtime.vm.code;

import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.vm.Fiber;

public abstract class BuiltinCode implements Code {
	public abstract void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException;

	@Override
	public void invoke(Fiber fiber, int args, Instance self) throws FourRuntimeException {
		Instance[] params = new Instance[args];
		for (int x = params.length - 1; x >= 0; x--)
			params[x] = fiber.pop();
		run(fiber, self, params);
	}
}
