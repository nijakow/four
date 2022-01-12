package nijakow.four.runtime;

import nijakow.four.runtime.vm.Fiber;

public class FClosure extends Instance {
	private final Instance self;
	private final Instance instance;
	private final Key key;
	
	public FClosure(Blue self, Instance instance, Key key) {
		this.self = self;
		this.instance = instance;
		this.key = key;
	}
	
	@Override
	public FClosure asFClosure() {
		return this;
	}

	@Override
	public void invoke(Fiber fiber, int args) throws FourRuntimeException {
		instance.extractMethod(fiber.getVM(), key).invoke(fiber, args, this.self);
	}
}
