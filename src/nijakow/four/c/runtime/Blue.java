package nijakow.four.c.runtime;

import java.util.HashMap;
import java.util.Map;

import nijakow.four.c.runtime.vm.Fiber;
import nijakow.four.c.runtime.vm.VM;
import nijakow.four.util.Pair;

public class Blue extends Instance {
	private static long ID_COUNTER = 0;
	private final long id;
	private boolean initialized = false;
	private Blueprint blueprint;
	private Map<Key, Instance> slots = new HashMap<>();
	
	private Blue() {
		this.id = ID_COUNTER++;
	}
	
	public Blue(Blueprint blueprint) {
		this();
		this.blueprint = blueprint;
	}
	
	private Blue(Blue other) {
		this();
		this.blueprint = other.blueprint;
		for (Key k : other.slots.keySet())
			slots.put(k, other.slots.get(k));
	}

	public boolean isInitialized() { return initialized; }
	public void setInitialized() { initialized = true; }
	
	@Override
	public String toString() {
		return blueprint.toString() + "#" + id;
	}
	
	@Override
	public boolean asBoolean() {
		return true;
	}
	
	@Override
	public Blue asBlue() {
		return this;
	}
	
	@Override
	public void loadSlot(Fiber fiber, Key key) {
		if (!slots.containsKey(key))
			super.loadSlot(fiber, key);
		else
			fiber.setAccu(slots.get(key));
	}
	
	@Override
	public void storeSlot(Fiber fiber, Key key, Instance value) {
		Pair<Type, Key> info = blueprint.getSlotInfo(key);
		if (info == null || !info.getFirst().check(value))
			throw new RuntimeException("Oof. Slot not found (or wrong type): " + key);
		slots.put(key, value);
	}
	
	@Override
	public void send(Fiber fiber, Key key, int args) {
		Code code = blueprint.getMethod(key);
		if (code == null)
			super.send(fiber, key, args);
		else
			code.invoke(fiber, args, this);
	}
	
	@Override
	public Code extractMethod(VM vm, Key key) {
		return blueprint.getMethod(key);
	}
	
	public Blue clone() {
		return new Blue(this);
	}
}
