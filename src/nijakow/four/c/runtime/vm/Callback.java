package nijakow.four.c.runtime.vm;

import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;

public class Callback {
	private final VM vm;
	private final Blue subject;
	private final Key message;
	
	public Callback(VM vm, Blue subject, Key message) {
		this.vm = vm;
		this.subject = subject;
		this.message = message;
	}
	
	public void invoke(Instance... args) {
		vm.startFiber(subject, message, args);
	}
}
