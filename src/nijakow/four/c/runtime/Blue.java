package nijakow.four.c.runtime;

import java.util.HashMap;
import java.util.Map;

import nijakow.four.c.runtime.vm.Fiber;

public class Blue extends Instance {
	private Blueprint blueprint;
	private Map<Key, Instance> slots = new HashMap<>();
	
	public Blue(Blueprint blueprint) {
		this.blueprint = blueprint;
	}

	@Override
	public boolean asBoolean() {
		return true;
	}
	
	@Override
	public void loadSlot(Fiber fiber, Key key) {
		if (!slots.containsKey(key))
			super.loadSlot(fiber, key);
		else
			fiber.setAccu(slots.get(key));
	}
	
	@Override
	public void storeSlot(Key key, Instance value) {
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
}
