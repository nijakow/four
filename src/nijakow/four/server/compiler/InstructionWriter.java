package nijakow.four.server.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import nijakow.four.share.lang.c.ast.OperatorType;
import nijakow.four.server.runtime.vm.code.ByteCode;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.ListType;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.vm.Bytecodes;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class InstructionWriter {	
	private final List<Byte> out = new ArrayList<>();
	private final List<Key> keys = new ArrayList<>();
	private final List<Instance> constants = new ArrayList<>();
	private final List<Type> types = new ArrayList<>();
	private final List<StreamPosition> tells = new ArrayList<>();
	private int maxLocal = 0;
	private int paramCount = 0;
	private boolean hasVarargs = false;
	
	public InstructionWriter() {
		
	}
	
	private void u8(int v) { out.add((byte) v); }
	private void u16(int v) { u8(v & 0xff); u8((v >> 8) & 0xff); }
	private void putU8(int i, int v) { out.set(i, (byte) (v & 0xff)); }
	private void putU16(int i, int v) { putU8(i, v & 0xff); putU8(i + 1, (v >> 8) & 0xff); }
	private void key(Key k) {
		int i = keys.indexOf(k);
		if (i >= 0) {
			u16(i);
		} else {
			u16(keys.size());
			keys.add(k);
		}
	}
	private void constant(Instance value) {
		int i = constants.indexOf(value);
		if (i >= 0) {
			u16(i);
		} else {
			u16(constants.size());
			constants.add(value);
		}
	}
	private void type(Type value) {
		int i = types.indexOf(value);
		if (i >= 0) {
			u16(i);
		} else {
			u16(types.size());
			types.add(value);
		}
	}
	private void tell(StreamPosition pos) {
		int i = tells.indexOf(pos);
		if (i >= 0) {
			u16(i);
		} else {
			u16(tells.size());
			tells.add(pos);
		}
	}

	public Integer getOffset() {
		return out.size();
	}
	
	public void declareParamCount(int pcount) {
		paramCount = pcount;
	}
	
	public void enableVarargs() {
		hasVarargs = true;
	}
	
	public Consumer<Integer> writePostponedJump() {
		u8(Bytecodes.BYTECODE_JUMP);
		int offset = getOffset();
		Consumer<Integer> c = (i) -> putU16(offset, i);
		u16(0);
		return c;
	}

	public Consumer<Integer> writePostponedJumpIfNot() {
		u8(Bytecodes.BYTECODE_JUMP_IF_NOT);
		int offset = getOffset();
		Consumer<Integer> c = (i) -> putU16(offset, i);
		u16(0);
		return c;
	}

	public void writeTypeCheck(Type type) {
		u8(Bytecodes.BYTECODE_TYPE_CHECK);
		type(type);
	}

	public void writeTell(StreamPosition pos) {
		u8(Bytecodes.BYTECODE_TELL);
		tell(pos);
	}

	public void writeLoadThis() {
		u8(Bytecodes.BYTECODE_LOAD_THIS);
	}

	public void writeLoadVANext() {
		u8(Bytecodes.BYTECODE_LOAD_VANEXT);
	}

	public void writeLoadVACount() {
		u8(Bytecodes.BYTECODE_LOAD_VACOUNT);
	}

	public void writeLoadConstant(Instance value) {
		u8(Bytecodes.BYTECODE_LOAD_CONSTANT);
		constant(value);
	}

	public void writeLoadLocal(int i) {
		u8(Bytecodes.BYTECODE_LOAD_LOCAL);
		u8(i);
		if (i > maxLocal) maxLocal = i;
	}

	public void writeStoreLocal(int i) {
		u8(Bytecodes.BYTECODE_STORE_LOCAL);
		u8(i);
		if (i > maxLocal) maxLocal = i;
	}

	public void writePush() {
		u8(Bytecodes.BYTECODE_PUSH);
	}
	
	public void writeReturn() {
		u8(Bytecodes.BYTECODE_RETURN);	
	}

	public void writeDot(Key k) {
		u8(Bytecodes.BYTECODE_LOAD_INST);
		key(k);
	}

	public void writeDotAssign(Key k) {
		u8(Bytecodes.BYTECODE_STORE_INST);
		key(k);
	}

	public void writeIndex() {
		u8(Bytecodes.BYTECODE_LOAD_INDEX);
	}
	
	public void writeIndexAssign() {
		u8(Bytecodes.BYTECODE_STORE_INDEX);
	}

	public void writeCall(int args, boolean hasVarargs) {
		u8(hasVarargs ? Bytecodes.BYTECODE_CALL_VARARGS : Bytecodes.BYTECODE_CALL);
		u8(args);
	}
	
	public void writeDotCall(Key k, int args, boolean hasVarargs) {
		u8(hasVarargs ? Bytecodes.BYTECODE_DOTCALL_VARARGS : Bytecodes.BYTECODE_DOTCALL);
		key(k);
		u8(args);
	}

	public void writeNew(Key clazz, int args, boolean hasVarargs) {
		u8(hasVarargs ? Bytecodes.BYTECODE_NEW_VARARGS : Bytecodes.BYTECODE_NEW);
		key(clazz);
		u8(args);
	}

	public void writeScope(Key key) {
		u8(Bytecodes.BYTECODE_SCOPE);
		key(key);
	}

	public void writeOp(OperatorType type) {
		u8(Bytecodes.BYTECODE_OP);
		u8(type.ordinal());
	}

	public void writeMakeList(ListType type, int length) {
		u8(Bytecodes.BYTECODE_MAKE_LIST);
		type(type);
		u16(length);
	}
	
	public void writeMakeMapping(int length) {
		u8(Bytecodes.BYTECODE_MAKE_MAPPING);
		u16(length);
	}

	public ByteCode finish() {
		byte[] bytes = new byte[out.size()];
		for (int i = 0; i < out.size(); i++)
			bytes[i] = out.get(i);
		if (maxLocal + 1 < paramCount)
			maxLocal = paramCount - 1;
		return new ByteCode(paramCount,
							hasVarargs,
							maxLocal + 1,
							bytes,
							keys.toArray(new Key[0]),
							constants.toArray(new Instance[0]),
							types.toArray(new Type[0]),
							tells.toArray(new StreamPosition[0]));
	}
}
