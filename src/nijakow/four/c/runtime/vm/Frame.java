package nijakow.four.c.runtime.vm;

import nijakow.four.c.ast.OperatorType;
import nijakow.four.c.parser.TokenType;
import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.Code;
import nijakow.four.c.runtime.FInteger;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;

public class Frame {
	private final Frame previous;
	private final Code code;
	private final Blue self;
	private final Instance[] locals;
	private int ip = 0;
	
	public Frame(Frame previous, Code code, Blue self) {
		this.previous = previous;
		this.code = code;
		this.self = self;
		this.locals = new Instance[code.getLocalCount()];
	}
	
	public void setLocal(int index, Instance value) {
		locals[index] = value;
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
			System.out.println("THIS");
			fiber.setAccu(self);
			break;
		case Bytecodes.BYTECODE_LOAD_CONSTANT:
			System.out.println("LOAD_CONSTANT");
			fiber.setAccu(code.constant(code.u16(ip)));
			ip += 2;
			break;
		case Bytecodes.BYTECODE_LOAD_LOCAL:
			System.out.println("LOAD_LOCAL");
			fiber.setAccu(locals[code.u8(ip++)]);
			break;
		case Bytecodes.BYTECODE_STORE_LOCAL:
			System.out.println("STORE_LOCAL");
			locals[code.u8(ip++)] = fiber.getAccu();
			break;
		case Bytecodes.BYTECODE_LOAD_INST:
			System.out.println("LOAD_INST");
			fiber.getAccu().loadSlot(fiber, code.keyAt(code.u16(ip)));
			ip += 2;
			break;
		case Bytecodes.BYTECODE_STORE_INST:
			System.out.println("STORE_INST");
			fiber.getAccu().storeSlot(code.keyAt(code.u16(ip)), fiber.pop());
			ip += 2;
			break;
		case Bytecodes.BYTECODE_PUSH:
			System.out.println("PUSH");
			fiber.push(fiber.getAccu());
			break;
		case Bytecodes.BYTECODE_DOTCALL:
			System.out.println("DOTCALL");
			Key key = code.keyAt(code.u16(ip));
			ip += 2;
			fiber.getAccu().send(fiber, key, code.u8(ip++));
			break;
		case Bytecodes.BYTECODE_JUMP:
			System.out.println("JUMP");
			ip = code.u16(ip);
			break;
		case Bytecodes.BYTECODE_JUMP_IF_NOT:
			System.out.print("JUMP_IF_NOT");
			if (fiber.getAccu().asBoolean()) {
				ip += 2;
				System.out.println(" (not performed)");
			} else {
				ip = code.u16(ip);
				System.out.println(" (performed)");
			}
			break;
		case Bytecodes.BYTECODE_OP:
			System.out.println("OP");
			int a1 = fiber.pop().asInt();
			int a2 = fiber.getAccu().asInt();
			fiber.setAccu(new FInteger(operate(OperatorType.values()[code.u8(ip++)], a1, a2)));
			break;
		case Bytecodes.BYTECODE_RETURN:
			System.out.println("RETURN");
			fiber.setTop(previous);
			break;
		default:
			System.out.println("???");
			break;
		}
	}
}
