package nijakow.four.c.runtime.vm;

public class Bytecodes {
	public static final byte BYTECODE_LOAD_THIS = 0x00;
	public static final byte BYTECODE_LOAD_CONSTANT = 0x01;
	public static final byte BYTECODE_LOAD_LOCAL = 0x02;
	public static final byte BYTECODE_STORE_LOCAL = 0x03;
	public static final byte BYTECODE_LOAD_INST = 0x04;
	public static final byte BYTECODE_STORE_INST = 0x05;
	public static final byte BYTECODE_PUSH = 0x06;
	public static final byte BYTECODE_CALL = 0x07;
	public static final byte BYTECODE_CALL_VARARGS = 0x08;
	public static final byte BYTECODE_DOTCALL = 0x09;
	public static final byte BYTECODE_DOTCALL_VARARGS = 0x0a;
	public static final byte BYTECODE_SCOPE = 0x0b;
	public static final byte BYTECODE_JUMP = 0x0c;
	public static final byte BYTECODE_JUMP_IF_NOT = 0x0d;
	public static final byte BYTECODE_RETURN = 0x0e;
	public static final byte BYTECODE_OP = 0x0f;
	public static final byte BYTECODE_LOAD_VANEXT = 0x10;
	public static final byte BYTECODE_LOAD_VACOUNT = 0x11;
	public static final byte BYTECODE_TYPE_CHECK = 0x12;
	public static final byte BYTECODE_TYPE_CAST = 0x13;
	public static final byte BYTECODE_MAKE_LIST = 0x14;
	public static final byte BYTECODE_LOAD_INDEX = 0x15;
	public static final byte BYTECODE_STORE_INDEX = 0x16;
}
