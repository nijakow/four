package nijakow.four.c.runtime;

import nijakow.four.c.runtime.fs.Filesystem;
import nijakow.four.c.runtime.vm.Fiber;
import nijakow.four.c.runtime.vm.VM;

public class FString extends Instance {
	private final String value;
	
	public FString(String value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		else if (!(obj instanceof FString)) return false;
		else return value.equals(((FString) obj).value);
	}
	
	@Override
	public boolean asBoolean() {
		return !value.isEmpty();
	}
	
	@Override
	public String asString() {
		return value;
	}
	
	@Override
	public Key asKey() {
		return Key.get(value);
	}
	
	@Override
	public FString asFString() {
		return this;
	}
	
	@Override
	public Instance plus(Instance y) {
		if (y instanceof FInteger) {
			return new FString(value + (char) y.asInt());
		} else {
			return new FString(value + y.asString());
		}
	}
	
	@Override
	public Instance index(Instance index) {
		return new FInteger(value.charAt(index.asInt()));
	}

	public Blue getBlue(Filesystem fs) {
		return fs.getBlue(value);
	}

	@Override
	public void loadSlot(Fiber fiber, Key key) {
		getBlue(fiber.getVM().getFilesystem()).loadSlot(fiber, key);
	}

	@Override
	public void storeSlot(Fiber fiber, Key key, Instance value) {
		getBlue(fiber.getVM().getFilesystem()).storeSlot(fiber, key, value);
	}

	@Override
	public void send(Fiber fiber, Key key, int args) {
		Blue blue = getBlue(fiber.getVM().getFilesystem());
		blue.send(fiber, key, args);
	}

	@Override
	public Code extractMethod(VM vm, Key key) {
		Blue blue = getBlue(vm.getFilesystem());
		return blue.extractMethod(vm, key);
	}
}
