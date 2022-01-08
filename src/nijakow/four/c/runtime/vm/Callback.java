package nijakow.four.c.runtime.vm;

import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.FClosure;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;

public class Callback {
	private final VM vm;
	private final FClosure closure;
	
	public Callback(VM vm, Blue subject, Key message) {
		this(vm, new FClosure(subject, subject, message));
	}
	
	public Callback(VM vm, FClosure closure) {
		this.vm = vm;
		this.closure = closure;
	}

	public void invoke(Instance... args) {
		vm.startFiber(closure, args);
	}
}
