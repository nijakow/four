package nijakow.four.c.runtime.vm;

public class Bytecodes {
	public static final byte BYTECODE_LOAD_THIS = 0x00;
	public static final byte BYTECODE_LOAD_CONSTANT = 0x01;
	public static final byte BYTECODE_LOAD_LOCAL = 0x02;
	public static final byte BYTECODE_STORE_LOCAL = 0x03;
	public static final byte BYTECODE_LOAD_INST = 0x04;
	public static final byte BYTECODE_STORE_INST = 0x05;
	public static final byte BYTECODE_PUSH = 0x06;
	public static final byte BYTECODE_DOTCALL = 0x07;
	public static final byte BYTECODE_DOTCALL_VARARGS = 0x08;
	public static final byte BYTECODE_JUMP = 0x09;
	public static final byte BYTECODE_JUMP_IF_NOT = 0x0a;
	public static final byte BYTECODE_RETURN = 0x0b;
	public static final int BYTECODE_OP = 0x0c;
}
