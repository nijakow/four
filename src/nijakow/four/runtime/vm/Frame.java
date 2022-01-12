package nijakow.four.runtime.vm;

import java.util.ArrayList;
import java.util.List;

import nijakow.four.c.ast.OperatorType;
import nijakow.four.runtime.*;

public class Frame {
	private final Frame previous;
	private final ByteCode code;
	private final Blue self;
	private final Instance[] locals;
	private final List<Instance> varargs = new ArrayList<>();
	private int ip = 0;
	
	public Frame(Frame previous, ByteCode code, Blue self) {
		this.previous = previous;
		this.code = code;
		this.self = self;
		this.locals = new Instance[code.getLocalCount()];
	}
	
	public void setLocal(int index, Instance value) {
		locals[index] = value;
	}
	
	public void addVararg(Instance value) {
		varargs.add(0, value);
	}
	
	private Instance operate(OperatorType type, Instance x, Instance y) throws FourRuntimeException {
		switch (type) {
		case PLUS: return x.plus(y);
		case MINUS: return new FInteger(x.asInt() - y.asInt());
		case MULT: return new FInteger(x.asInt() * y.asInt());
		case DIV: return new FInteger(x.asInt() / y.asInt());
		case MOD: return new FInteger(x.asInt() % y.asInt());
		case EQ: return x.equals(y) ? new FInteger(1) : new FInteger(0);
		case INEQ: return x.equals(y) ? new FInteger(0) : new FInteger(1);
		case LESS: return (x.asInt() < y.asInt()) ? new FInteger(1) : new FInteger(0);
		case GREATER: return (x.asInt() > y.asInt()) ? new FInteger(1) : new FInteger(0);
		case LEQ: return (x.asInt() <= y.asInt()) ? new FInteger(1) : new FInteger(0);
		case GEQ: return (x.asInt() >= y.asInt()) ? new FInteger(1) : new FInteger(0);
		case SHL: return new FInteger(x.asInt() << y.asInt());
		case SHR: return new FInteger(x.asInt() >> y.asInt());
		case BITAND: return new FInteger(x.asInt() & y.asInt());
		case BITOR: return new FInteger(x.asInt() | y.asInt());
		case BITXOR: return new FInteger(x.asInt() ^ y.asInt()); 
		default: 
			throw new FourRuntimeException("Whoopsie, didn't catch the op type!");
		}
	}
	
	public void tick(Fiber fiber) throws FourRuntimeException {
		if (!code.boundsCheck(ip)) {
			fiber.setTop(previous);
			return;
		}
		
		switch (code.u8(ip++)) {
		case Bytecodes.BYTECODE_LOAD_THIS:
			fiber.setAccu(self);
			break;
		case Bytecodes.BYTECODE_LOAD_CONSTANT:
			fiber.setAccu(code.constant(code.u16(ip)));
			ip += 2;
			break;
		case Bytecodes.BYTECODE_LOAD_LOCAL:
			fiber.setAccu(locals[code.u8(ip++)]);
			break;
		case Bytecodes.BYTECODE_STORE_LOCAL:
			locals[code.u8(ip++)] = fiber.getAccu();
			break;
		case Bytecodes.BYTECODE_LOAD_INST:
			fiber.getAccu().loadSlot(fiber, code.keyAt(code.u16(ip)));
			ip += 2;
			break;
		case Bytecodes.BYTECODE_STORE_INST: {
			Instance value = fiber.pop();
			fiber.getAccu().storeSlot(fiber, code.keyAt(code.u16(ip)), value);
			ip += 2;
			fiber.setAccu(value);
			break;
		}
		case Bytecodes.BYTECODE_LOAD_INDEX:
			fiber.setAccu(fiber.pop().index(fiber.getAccu()));
			break;
		case Bytecodes.BYTECODE_STORE_INDEX: {
			Instance index = fiber.pop();
			fiber.setAccu(fiber.pop().putIndex(index, fiber.getAccu()));
			break;
		}
		case Bytecodes.BYTECODE_PUSH:
			fiber.push(fiber.getAccu());
			break;
		case Bytecodes.BYTECODE_CALL:
			fiber.getAccu().invoke(fiber, code.u8(ip++));
			break;
		case Bytecodes.BYTECODE_CALL_VARARGS:
			int args = code.u8(ip++);
			for (Instance v : varargs) {
				fiber.push(v);
				args++;
			}
			fiber.getAccu().invoke(fiber, args);
			break;
		case Bytecodes.BYTECODE_DOTCALL:
			Key key = code.keyAt(code.u16(ip));
			ip += 2;
			fiber.getAccu().send(fiber, key, code.u8(ip++));
			break;
		case Bytecodes.BYTECODE_DOTCALL_VARARGS:
			key = code.keyAt(code.u16(ip));
			ip += 2;
			args = code.u8(ip++);
			for (Instance v : varargs) {
				fiber.push(v);
				args++;
			}
			fiber.getAccu().send(fiber, key, args);
			break;
		case Bytecodes.BYTECODE_SCOPE:
			key = code.keyAt(code.u16(ip));
			ip += 2;
			fiber.setAccu(new FClosure(self, fiber.getAccu(), key));
			break;
		case Bytecodes.BYTECODE_JUMP:
			ip = code.u16(ip);
			break;
		case Bytecodes.BYTECODE_JUMP_IF_NOT:
			if (fiber.getAccu().asBoolean()) {
				ip += 2;
			} else {
				ip = code.u16(ip);
			}
			break;
		case Bytecodes.BYTECODE_OP: {
			OperatorType operation = OperatorType.values()[code.u8(ip++)];
			if (operation == OperatorType.LOGNOT) {
				if (fiber.getAccu().asBoolean()) {
					fiber.setAccu(new FInteger(0));
				} else {
					fiber.setAccu(new FInteger(1));
				}
			} else if (operation == OperatorType.UPLUS) {
				// Do nothing
			} else if (operation == OperatorType.UMINUS) {
				fiber.setAccu(new FInteger(-fiber.getAccu().asInt()));
			} else if (operation == OperatorType.LENGTH) {
				fiber.setAccu(new FInteger(fiber.getAccu().length()));
			} else {
				Instance a1 = fiber.pop();
				Instance a2 = fiber.getAccu();
				fiber.setAccu(operate(operation, a1, a2));
			}
			break;
		}
		case Bytecodes.BYTECODE_RETURN:
			fiber.setTop(previous);
			break;
		case Bytecodes.BYTECODE_LOAD_VANEXT:
			fiber.setAccu(varargs.remove(0));
			break;
		case Bytecodes.BYTECODE_LOAD_VACOUNT:
			fiber.setAccu(new FInteger(varargs.size()));
			break;
		case Bytecodes.BYTECODE_TYPE_CHECK:
			Type type = code.type(code.u16(ip));
			ip += 2;
			type.expect(fiber.getAccu());
			break;
		case Bytecodes.BYTECODE_TYPE_CAST:
			type = code.type(code.u16(ip));
			ip += 2;
			fiber.setAccu(type.cast(fiber.getAccu()));
			break;
		case Bytecodes.BYTECODE_MAKE_LIST: {
			ListType ltype = (ListType) code.type(code.u16(ip));
			ip += 2;
			int length = code.u16(ip);
			ip += 2;
			Instance[] instances = new Instance[length];
			while (length --> 0)
				instances[length] = fiber.pop();
			fiber.setAccu(new FList(ltype, instances));
			break;
		}
		case Bytecodes.BYTECODE_MAKE_MAPPING: {
			int length = code.u16(ip);
			ip += 2;
			Instance[] instances = new Instance[length];
			while (length --> 0)
				instances[length] = fiber.pop();
			fiber.setAccu(new FMapping(instances));
			break;
		}
		default:
			throw new FourRuntimeException("Unknown bytecode!");
		}
	}
}
