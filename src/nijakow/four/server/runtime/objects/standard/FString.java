package nijakow.four.server.runtime.objects.standard;

import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.FloatingInstance;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.vm.fiber.Fiber;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.runtime.vm.VM;

public class FString extends FloatingInstance {
	private final String value;
	
	public FString(String value) {
		this.value = value;
		registerToPool();
	}

	@Override
	public String toString() {
		boolean endReached = true;
		StringBuilder sb = new StringBuilder("\"");
		for (int x = 0; x < this.value.length(); x++) {
			if (x >= 24) {
				sb.append("\" [...]");
				endReached = false;
				break;
			}
			char c = this.value.charAt(x);
			if (c >= 32 && c < 127)
				sb.append(c);
			else
				sb.append("\\x" + String.format("%02x", (int) c));
		}
		if (endReached)
			sb.append('\"');
		return sb.toString();
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
	public String getType() { return "string"; }

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
	public Instance index(Instance index) throws FourRuntimeException {
		try {
			int i = index.asInt();
			if (i < 0) i = value.length() + i;
			return FInteger.get(value.charAt(i));
		} catch (StringIndexOutOfBoundsException e) {
			throw new FourRuntimeException("String index " + index.asInt() + " out of bounds!");
		}
	}

	@Override
	public int length() {
		return value.length();
	}

	private Blue getBlue(VM vm, NVFileSystem fs) throws CompilationException, ParseException {
		return fs.getBlue(vm, value);
	}

	@Override
	public Code extractMethod(VM vm, Key key) throws CompilationException, ParseException {
		Blue blue = getBlue(vm, vm.getFilesystem());
		return blue.extractMethod(vm, key);
	}

	@Override
	public void loadSlot(Fiber fiber, Key key) throws FourRuntimeException {
		if (key == Key.get("length"))
			fiber.setAccu(FInteger.get(length()));
		else
			super.loadSlot(fiber, key);
	}
}
