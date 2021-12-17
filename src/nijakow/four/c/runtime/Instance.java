package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public abstract class Instance {

	public boolean asBoolean() { return true; }
	public int asInt() { return hashCode(); }
	
	public void loadSlot(Fiber fiber, Key key) {
		throw new RuntimeException("Oof. Slot not found.");
	}
	
	public void storeSlot(Key key, Instance value) {
		throw new RuntimeException("Oof. Slot not found.");
	}
	
	public void send(Fiber fiber, Key key, int args) {
		throw new RuntimeException("Oof. Method not found.");
	}
}
