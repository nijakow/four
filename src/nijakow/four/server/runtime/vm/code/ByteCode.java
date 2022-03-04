package nijakow.four.server.runtime.vm.code;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.exceptions.CastException;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.vm.Fiber;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ByteCode implements Code {
	private final int params;
	private final boolean hasVarargs;
	private final int locals;
	private final byte[] bytecodes;
	private final Key[] keys;
	private final Instance[] constants;
	private final Type[] types;
	private final StreamPosition[] tells;
	
	public ByteCode(int params, boolean hasVarargs, int locals, byte[] bytecodes, Key[] keys, Instance[] constants, Type[] types, StreamPosition[] tells) {
		this.params = params;
		this.hasVarargs = hasVarargs;
		this.locals = locals;
		this.bytecodes = bytecodes;
		this.keys = keys;
		this.constants = constants;
		this.types = types;
		this.tells = tells;
	}

	public int getLocalCount() {
		return locals;
	}

	public int getFixedArgCount() {
		return params;
	}

	public int u8(int ip) {
		return bytecodes[ip] & 0xff;
	}

	public int u16(int ip) {
		return (bytecodes[ip] & 0xff) | ((bytecodes[ip + 1] & 0xff) << 8);
	}

	public Key keyAt(int index) {
		return keys[index];
	}

	public Instance constant(int index) {
		return constants[index];
	}
	
	public Type type(int index) {
		return types[index];
	}

	public StreamPosition tell(int index) { return tells[index]; }

	public boolean argCheck(int args) {
		return (args == params) || (hasVarargs && args > params);
	}
	
	public boolean boundsCheck(int ip) {
		return ip >= 0 && ip < bytecodes.length;
	}

	@Override
	public void invoke(Fiber fiber, int args, Instance self) throws CastException {
		fiber.enter(self.asBlue(), this, args);
	}
}
