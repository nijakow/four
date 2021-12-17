package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public abstract class Instance {

	public boolean asBoolean() { return true; }
	public int asInt() { return hashCode(); }
	public String asString() { return toString(); }
	public Key asKey() { throw new RuntimeException("Conversion failed!"); }
	public Blue asBlue() { throw new RuntimeException("Conversion failed!"); }
	public FConnection asFConnection() { throw new RuntimeException("Conversion failed!"); }
	
	public void loadSlot(Fiber fiber, Key key) {
		throw new RuntimeException("Oof. Slot not found.");
	}
	
	public void storeSlot(Fiber fiber, Key key, Instance value) {
		throw new RuntimeException("Oof. Slot not found.");
	}
	
	public void send(Fiber fiber, Instance subject, Key key, int args) {
		throw new RuntimeException("Oof. Method not found.");
	}
	
	public final void send(Fiber fiber, Key key, int args) {
		send(fiber, this, key, args);
	}
}
