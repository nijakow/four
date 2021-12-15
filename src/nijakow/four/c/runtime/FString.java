package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public class FString extends Instance {
	private final String value;
	
	public FString(String value) {
		this.value = value;
	}
	
	@Override
	public boolean asBoolean() {
		return !value.isEmpty();
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
			System.out.println(value);
		} else {
			throw new RuntimeException("Oof.");
		}
	}
}
