package nijakow.four.runtime.vm;

import nijakow.four.runtime.Blue;
import nijakow.four.runtime.FClosure;
import nijakow.four.runtime.Instance;
import nijakow.four.runtime.Key;

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
