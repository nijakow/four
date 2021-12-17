package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Fiber;

public class ByteCode implements Code {
	private final int params;
	private final boolean hasVarargs;
	private final int locals;
	private final byte[] bytecodes;
	private final Key[] keys;
	private final Instance[] constants;
	
	public ByteCode(int params, boolean hasVarargs, int locals, byte[] bytecodes, Key[] keys, Instance[] constants) {
		this.params = params;
		this.hasVarargs = hasVarargs;
		this.locals = locals;
		this.bytecodes = bytecodes;
		this.keys = keys;
		this.constants = constants;
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

	public boolean argCheck(int args) {
		return (args == params) || (hasVarargs && args > params);
	}
	
	public boolean boundsCheck(int ip) {
		return ip >= 0 && ip < bytecodes.length;
	}

	@Override
	public void invoke(Fiber fiber, int args, Instance self) {
		fiber.enter(self.asBlue(), this, args);
	}
}
