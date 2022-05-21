package nijakow.four.server.runtime.vm.code;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.vm.Bytecodes;
import nijakow.four.server.runtime.vm.fiber.Fiber;
import nijakow.four.share.lang.c.ast.OperatorType;
import nijakow.four.share.lang.base.parser.StreamPosition;

import java.io.PrintStream;

public class ByteCode implements Code {
	private final CodeMeta meta;
	private final int params;
	private final boolean hasVarargs;
	private final int locals;
	private final byte[] bytecodes;
	private final Key[] keys;
	private final Instance[] constants;
	private final Type[] types;
	private final StreamPosition[] tells;
	
	public ByteCode(CodeMeta meta, int params, boolean hasVarargs, int locals, byte[] bytecodes, Key[] keys, Instance[] constants, Type[] types, StreamPosition[] tells) {
		this.meta = meta;
		this.params = params;
		this.hasVarargs = hasVarargs;
		this.locals = locals;
		this.bytecodes = bytecodes;
		this.keys = keys;
		this.constants = constants;
		this.types = types;
		this.tells = tells;
	}

	@Override
	public ByteCode asByteCode() {
		return this;
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
	public void invoke(Fiber fiber, int args, Instance self) throws FourRuntimeException {
		fiber.enter(self.asBlue(), this, args);
	}

	public CodeMeta getMeta() { return meta; }

	public void disassemble(PrintStream writer) {
		// TODO: Cache this string?
		final ByteCode code = this;
		int ip = 0;
		while (code.boundsCheck(ip)) {
			writer.print(String.format("%4d:   ", ip));
			switch (code.u8(ip++)) {
				case Bytecodes.BYTECODE_LOAD_THIS:
					writer.println("LOAD_THIS");
					break;
				case Bytecodes.BYTECODE_LOAD_CONSTANT:
					writer.println("CONST " + code.constant(code.u16(ip)));
					ip += 2;
					break;
				case Bytecodes.BYTECODE_LOAD_LOCAL:
					writer.println("LOAD_LOCAL " + code.u8(ip++));
					break;
				case Bytecodes.BYTECODE_STORE_LOCAL:
					writer.println("STORE_LOCAL " + code.u8(ip++));
					break;
				case Bytecodes.BYTECODE_LOAD_INST:
					writer.println("LOAD " + code.keyAt(code.u16(ip)));
					ip += 2;
					break;
				case Bytecodes.BYTECODE_STORE_INST: {
					writer.println("STORE " + code.keyAt(code.u16(ip)));
					ip += 2;
					break;
				}
				case Bytecodes.BYTECODE_LOAD_INDEX:
					writer.println("INDEX");
					break;
				case Bytecodes.BYTECODE_STORE_INDEX: {
					writer.println("STORE_INDEX");
					break;
				}
				case Bytecodes.BYTECODE_PUSH:
					writer.println("PUSH");
					break;
				case Bytecodes.BYTECODE_CALL:
					writer.println("CALL " + code.u8(ip++));
					break;
				case Bytecodes.BYTECODE_CALL_VARARGS:
					writer.println("CALL_VARARGS " + code.u8(ip++));
					break;
				case Bytecodes.BYTECODE_DOTCALL:
					writer.println("DOTCALL " + code.keyAt(code.u16(ip)) + " " + code.u8(ip + 2));
					ip += 3;
					break;
				case Bytecodes.BYTECODE_DOTCALL_VARARGS:
					writer.println("DOTCALL_VARARGS " + code.keyAt(code.u16(ip)) + " " + code.u8(ip + 2));
					ip += 3;
					break;
				case Bytecodes.BYTECODE_NEW:
					writer.println("NEW " + code.keyAt(code.u16(ip)) + " " + code.u8(ip + 2));
					ip += 3;
					break;
				case Bytecodes.BYTECODE_NEW_VARARGS:
					writer.println("NEW_VARARGS " + code.keyAt(code.u16(ip)) + " " + code.u8(ip + 2));
					ip += 3;
					break;
				case Bytecodes.BYTECODE_SCOPE: {
					writer.println("SCOPE " + code.u8(ip) + " " + code.keyAt(code.u16(ip + 1)));
					ip += 3;
					break;
				}
				case Bytecodes.BYTECODE_JUMP:
					writer.println("JUMP " + code.u16(ip));
					ip += 2;
					break;
				case Bytecodes.BYTECODE_JUMP_IF_NOT:
					writer.println("JUMP_IF_NOT " + code.u16(ip));
					ip += 2;
					break;
				case Bytecodes.BYTECODE_OP: {
					writer.println("OP <" + OperatorType.values()[code.u8(ip++)].name() + ">");
					break;
				}
				case Bytecodes.BYTECODE_RETURN:
					writer.println("RETURN");
					break;
				case Bytecodes.BYTECODE_LOAD_VANEXT:
					writer.println("VA_NEXT");
					break;
				case Bytecodes.BYTECODE_LOAD_VACOUNT:
					writer.println("VA_COUNT");
					break;
				case Bytecodes.BYTECODE_TYPE_CHECK:
					writer.println("TYPE_CHECK " + code.type(code.u16(ip)));
					ip += 2;
					break;
				case Bytecodes.BYTECODE_TYPE_CAST:
					writer.println("TYPE_CAST " + code.type(code.u16(ip)));
					ip += 2;
					break;
				case Bytecodes.BYTECODE_MAKE_LIST: {
					writer.println("MAKE_LIST " + code.type(code.u16(ip)) + " " + code.u16(ip + 2));
					ip += 4;
					break;
				}
				case Bytecodes.BYTECODE_MAKE_MAPPING: {
					writer.println("MAKE_MAPPING " + code.u16(ip));
					ip += 2;
					break;
				}
				case Bytecodes.BYTECODE_TELL: {
					writer.println("(TELL) " + code.tell(code.u16(ip)));
					ip += 2;
					break;
				}
				default:
					writer.println("???");
			}
		}
	}
}
