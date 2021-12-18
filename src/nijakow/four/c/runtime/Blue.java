package nijakow.four.c.runtime;

import java.util.HashMap;
import java.util.Map;

import nijakow.four.c.runtime.vm.Fiber;
import nijakow.four.util.Pair;

public class Blue extends Instance {
	private Blueprint blueprint;
	private Map<Key, Instance> slots = new HashMap<>();
	
	public Blue(Blueprint blueprint) {
		this.blueprint = blueprint;
	}
	
	private Blue(Blue other) {
		this.blueprint = other.blueprint;
		for (Key k : other.slots.keySet())
			slots.put(k, other.slots.get(k));
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
			throw new RuntimeException("Oof. Slot not found (or wrong type)...");
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
	public Code extractMethod(Fiber fiber, Key key) {
		return blueprint.getMethod(key);
	}
	
	public Blue clone() {
		return new Blue(this);
	}
}
