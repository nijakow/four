package nijakow.four.runtime;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.runtime.fs.Filesystem;
import nijakow.four.runtime.vm.Fiber;
import nijakow.four.runtime.vm.VM;

public class FString extends Instance {
	private final String value;
	
	public FString(String value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
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
		try {
			int i = index.asInt();
			if (i < 0) i = value.length() + i;
			return new FInteger(value.charAt(i));
		} catch (StringIndexOutOfBoundsException e) {
			return new FInteger(0);
		}
	}

	@Override
	public int length() {
		return value.length();
	}

	public Blue getBlueWithErrors(Filesystem fs) throws CompilationException, ParseException {
		return fs.getBlueWithErrors(value);
	}

	/*public Blue getBlue(Filesystem fs) {
		return fs.getBlue(value);
	}*/

	@Override
	public void loadSlot(Fiber fiber, Key key) throws FourRuntimeException {
		getBlueWithErrors(fiber.getVM().getFilesystem()).loadSlot(fiber, key);
	}

	@Override
	public void storeSlot(Fiber fiber, Key key, Instance value) throws FourRuntimeException {
		getBlueWithErrors(fiber.getVM().getFilesystem()).storeSlot(fiber, key, value);
	}

	@Override
	public void send(Fiber fiber, Key key, int args) throws FourRuntimeException {
		Blue blue = getBlueWithErrors(fiber.getVM().getFilesystem());
		blue.send(fiber, key, args);
	}

	@Override
	public Code extractMethod(VM vm, Key key) throws CompilationException, ParseException {
		Blue blue = getBlueWithErrors(vm.getFilesystem());
		return blue.extractMethod(vm, key);
	}
}
