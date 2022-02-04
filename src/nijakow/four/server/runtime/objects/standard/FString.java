package nijakow.four.server.runtime.objects.standard;

import nijakow.four.server.runtime.objects.FloatingInstance;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.ast.ASTExpression;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.runtime.vm.VM;
import nijakow.four.server.serialization.base.ISerializer;
import nijakow.four.share.lang.c.parser.Parser;
import nijakow.four.share.lang.c.parser.StringCharStream;
import nijakow.four.share.lang.c.parser.Tokenizer;

public class FString extends FloatingInstance {
	private final String value;
	
	public FString(String value) {
		this.value = value;
		registerToPool();
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

    public Code compileAsCode() throws ParseException, CompilationException {
		Parser parser = new Parser(new Tokenizer(new StringCharStream(this.value)));
		ASTExpression expr = parser.parseLine();
		return expr.compileStandalone();
    }
}
