package nijakow.four.runtime;

import nijakow.four.runtime.vm.Fiber;
import nijakow.four.runtime.vm.VM;

public abstract class Instance {
	private static final Instance NIL = new Instance() {};

	public static Instance getNil() { return NIL; }
	
	public boolean isNil() { return this == NIL; }
	
	public boolean asBoolean() { return !isNil(); }
	public int asInt() { return hashCode(); }
	public String asString() { return toString(); }
	public Key asKey() { throw new RuntimeException("Conversion failed!"); }
	public Blue asBlue() { throw new RuntimeException("Conversion failed!"); }
	public FString asFString() { throw new RuntimeException("Conversion failed!"); }
	public FList asFList() { throw new RuntimeException("Conversion failed!"); }
	public FMapping asFMapping() { throw new RuntimeException("Conversion failed!"); }
	public FClosure asFClosure() { throw new RuntimeException("Conversion failed!"); }
	public FConnection asFConnection() { throw new RuntimeException("Conversion failed!"); }
	
	public Instance plus(Instance y) { throw new RuntimeException("Addition failed!"); }
	
	public Instance index(Instance index) { throw new RuntimeException("Invalid index!"); }
	public Instance putIndex(Instance index, Instance value) throws CastException { throw new RuntimeException("Invalid index!"); }
	public int length() { return 0; }

	public void invoke(Fiber fiber, int args) {
		throw new RuntimeException("Oof. Can't invoke this object!");
	}
	
	public void loadSlot(Fiber fiber, Key key) {
		throw new RuntimeException("Oof. Slot not found: " + key);
	}
	
	public void storeSlot(Fiber fiber, Key key, Instance value) {
		throw new RuntimeException("Oof. Slot not found.");
	}
	
	public void send(Fiber fiber, Key key, int args) {
		throw new RuntimeException("Oof. Method not found: " + key);
	}
	
	public Code extractMethod(VM vm, Key key) {
		throw new RuntimeException("Oof. Can't extract method.");
	}
}
