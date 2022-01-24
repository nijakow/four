package nijakow.four.runtime.objects;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.runtime.vm.code.Code;
import nijakow.four.runtime.Key;
import nijakow.four.runtime.nvfs.NVFileSystem;
import nijakow.four.runtime.vm.VM;
import nijakow.four.serialization.base.ISerializer;

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

	private Blue getBlue(NVFileSystem fs) throws CompilationException, ParseException {
		return fs.getBlue(value);
	}

	@Override
	public Code extractMethod(VM vm, Key key) throws CompilationException, ParseException {
		Blue blue = getBlue(vm.getFilesystem());
		return blue.extractMethod(vm, key);
	}

	@Override
	public String getSerializationClassID() {
		return "string";
	}

	@Override
	public void serialize(ISerializer serializer) {
		serializer.writeString(value);
	}
}
