package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public abstract class BuiltinCode implements Code {
	abstract void run(Fiber fiber, Blue self, Instance[] args);

	@Override
	public void invoke(Fiber fiber, int args, Blue self) {
		Instance[] params = new Instance[args];
		for (int x = params.length - 1; x >= 0; x--)
			params[x] = fiber.pop();
		run(fiber, self, params);
	}
}
