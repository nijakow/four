package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public class FClosure extends Instance {
	private final Instance self;
	private final Code code;
	
	public FClosure(Blue self, Code code) {
		this.self = self;
		this.code = code;
	}

	@Override
	public void invoke(Fiber fiber, int args) {
		code.invoke(fiber, args, this.self);
	}
}
