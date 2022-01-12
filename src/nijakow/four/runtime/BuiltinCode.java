package nijakow.four.runtime;

import nijakow.four.runtime.vm.Fiber;

public abstract class BuiltinCode implements Code {
	abstract void run(Fiber fiber, Instance self, Instance[] args);

	@Override
	public void invoke(Fiber fiber, int args, Instance self) {
		Instance[] params = new Instance[args];
		for (int x = params.length - 1; x >= 0; x--)
			params[x] = fiber.pop();
		run(fiber, self, params);
	}
}
