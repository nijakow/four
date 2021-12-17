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
		// TODO: Cache this!
		FSNode node = fs.find(value);
		if (node == null || node.asFile() == null) {
			return null;
		} else {
			return node.asFile().getInstance();
		}
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
		getBlue(fiber.getVM().getFilesystem()).send(fiber, key, args);
	}
}
