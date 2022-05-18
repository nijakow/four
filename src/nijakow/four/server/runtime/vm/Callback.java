package nijakow.four.server.runtime.vm;

import nijakow.four.server.runtime.*;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.objects.standard.FClosure;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.vm.fiber.Fiber;
import nijakow.four.server.process.Process;

public class Callback {
	private final VM vm;
	private final Process state;
	private final FClosure closure;
	
	public Callback(VM vm, Process state, Blue subject, Key message) {
		this(vm, state, new FClosure(null, subject, subject, message));
	}
	
	public Callback(VM vm, Process state, FClosure closure) {
		this.vm = vm;
		this.state = state;
		this.closure = closure;
	}

	public Fiber invoke(Instance... args) throws FourRuntimeException {
		return vm.startFiber(state, closure, args);
	}
}
