package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public abstract class Instance {
	private static final Instance NIL = new Instance() {};

	public static Instance getNil() { return NIL; }
	
	public boolean isNil() { return this == NIL; }
	
	public boolean asBoolean() { return !isNil(); }
	public int asInt() { return hashCode(); }
	public String asString() { return toString(); }
	public Key asKey() { throw new RuntimeException("Conversion failed!"); }
	public Blue asBlue() { throw new RuntimeException("Conversion failed!"); }
	public FConnection asFConnection() { throw new RuntimeException("Conversion failed!"); }
	
	public void invoke(Fiber fiber, int args) {
		throw new RuntimeException("Oof. Can't invoke this object!");
	}
	
	public void loadSlot(Fiber fiber, Key key) {
		throw new RuntimeException("Oof. Slot not found.");
	}
	
	public void storeSlot(Fiber fiber, Key key, Instance value) {
		throw new RuntimeException("Oof. Slot not found.");
	}
	
	public void send(Fiber fiber, Key key, int args) {
		throw new RuntimeException("Oof. Method not found.");
	}
	
	public Code extractMethod(Fiber fiber, Key key) {
		throw new RuntimeException("Oof. Can't extract method.");
	}
}
