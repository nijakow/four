package nijakow.four.server.runtime.vm;

import nijakow.four.server.runtime.*;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.Blue;
import nijakow.four.server.runtime.objects.FClosure;
import nijakow.four.server.runtime.objects.Instance;

public class Callback {
	private final VM vm;
	private final SharedFiberState state;
	private final FClosure closure;
	
	public Callback(VM vm, SharedFiberState state, Blue subject, Key message) {
		this(vm, state, new FClosure(subject, subject, message));
	}
	
	public Callback(VM vm, SharedFiberState state, FClosure closure) {
		this.vm = vm;
		this.state = state;
		this.closure = closure;
	}

	public void invoke(Instance... args) throws FourRuntimeException {
		vm.startFiber(state, closure, args);
	}
}
