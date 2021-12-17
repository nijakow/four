package nijakow.four.c.runtime.vm;

import java.util.ArrayList;
import java.util.List;

import nijakow.four.c.ast.OperatorType;
import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.ByteCode;
import nijakow.four.c.runtime.FClosure;
import nijakow.four.c.runtime.FInteger;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;

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
	
	private int operate(OperatorType type, int x, int y) {
		switch (type) {
		case PLUS: return x + y;
		case MINUS: return x - y;
		case MULT: return x * y;
		case DIV: return x / y;
		case MOD: return x % y;
		case EQ: return (x == y) ? 1 : 0;
		case INEQ: return (x != y) ? 1 : 0;
		case LESS: return (x < y) ? 1 : 0;
		case GREATER: return (x > y) ? 1 : 0;
		case LEQ: return (x <= y) ? 1 : 0;
		case GEQ: return (x >= y) ? 1 : 0;
		case SHL: return x << y;
		case SHR: return x >> y;
		case BITAND: return x & y;
		case BITOR: return x | y;
		case BITXOR: return x ^ y; 
		default: 
			throw new RuntimeException("Whoopsie, didn't catch the op type!");
		}
	}
	
	public void tick(Fiber fiber) {
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
		case Bytecodes.BYTECODE_STORE_INST:
			fiber.getAccu().storeSlot(fiber, code.keyAt(code.u16(ip)), fiber.pop());
			ip += 2;
			break;
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
			fiber.setAccu(new FClosure(self, fiber.getAccu().extractMethod(fiber, key)));
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
		case Bytecodes.BYTECODE_OP:
			int a1 = fiber.pop().asInt();
			int a2 = fiber.getAccu().asInt();
			fiber.setAccu(new FInteger(operate(OperatorType.values()[code.u8(ip++)], a1, a2)));
			break;
		case Bytecodes.BYTECODE_RETURN:
			fiber.setTop(previous);
			break;
		case Bytecodes.BYTECODE_LOAD_VANEXT:
			fiber.setAccu(varargs.remove(0));
			break;
		default:
			System.out.println("???");
			break;
		}
	}
}
