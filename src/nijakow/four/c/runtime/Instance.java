package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public abstract class Instance {

	public boolean asBoolean() { return true; }
	public int asInt() { return hashCode(); }
	
	public abstract void loadSlot(Fiber fiber, Key key);
	public abstract void storeSlot(Key key, Instance value);
	public abstract void send(Fiber fiber, Key key, int args);
}
