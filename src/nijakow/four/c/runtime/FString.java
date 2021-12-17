package nijakow.four.c.runtime;

import nijakow.four.c.runtime.fs.FSNode;
import nijakow.four.c.runtime.fs.Filesystem;
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
	public String asString() {
		return value;
	}
	
	@Override
	public Key asKey() {
		return Key.get(value);
	}
	
	private Blue getBlue(Filesystem fs) {
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
	public void send(Fiber fiber, Instance subject, Key key, int args) {
		Blue blue = getBlue(fiber.getVM().getFilesystem());
		blue.send(fiber, blue, key, args);
	}
}
