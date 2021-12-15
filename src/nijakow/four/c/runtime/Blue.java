package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public class Blue extends Instance {
	private Blueprint blueprint;
	private Instance slots[];
	
	public Blue(Blueprint blueprint, int slots) {
		this.blueprint = blueprint;
		this.slots = new Instance[slots];	// TODO: Initialize
	}

	@Override
	public boolean asBoolean() {
		return true;
	}
	
	@Override
	public void loadSlot(Fiber fiber, Key key) {
		Integer i = blueprint.getSlotIndex(key);
		if (i < 0)
			throw new RuntimeException("Aaargh! Slot not found!");
		fiber.setAccu(slots[i]);
	}
	
	@Override
	public void storeSlot(Key key, Instance value) {
		Integer i = blueprint.getSlotIndex(key);
		if (i < 0)
			throw new RuntimeException("Aaargh! Slot not found!");
		slots[i] = value;
	}
	
	@Override
	public void send(Fiber fiber, Key key, int args) {
		Code code = blueprint.getMethod(key);
		if (code == null)
			throw new RuntimeException("Aaargh! Method not found!");
		fiber.enter(this, code, args);
	}
}
