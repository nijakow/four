package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public class FInteger extends Instance {
	private final int value;
	
	public FInteger(int value) {
		this.value = value;
	}
	
	@Override
	public boolean asBoolean() {
		return value != 0;
	}

	@Override
	public int asInt() {
		return value;
	}

	@Override
	public void loadSlot(Fiber fiber, Key key) {
		throw new RuntimeException("Oof.");
	}

	@Override
	public void storeSlot(Key key, Instance value) {
		throw new RuntimeException("Oof.");
	}

	@Override
	public void send(Fiber fiber, Key key, int args) {
		if (key == Key.get("print")) {
			System.out.println("Int: " + value);
		} else {
			throw new RuntimeException("Oof.");
		}
	}
}
