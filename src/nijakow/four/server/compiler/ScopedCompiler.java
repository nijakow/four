package nijakow.four.server.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.Label;
import nijakow.four.share.lang.c.ast.OperatorType;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.ListType;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.util.Pair;

class ScopedCompilerLabel implements Label {
	private final ScopedCompiler compiler;
	private final InstructionWriter writer;
	private final List<Consumer<Integer>> pending = new ArrayList<>();
	private Integer offset = null;
	
	ScopedCompilerLabel(ScopedCompiler compiler, InstructionWriter writer) {
		this.compiler = compiler;
		this.writer = writer;
	}

	@Override
	public void compileJump() {
		Consumer<Integer> function = writer.writePostponedJump();
		if (offset != null) function.accept(offset);
		else pending.add(function);
	}

	@Override
	public void compileJumpIfNot() {
		Consumer<Integer> function = writer.writePostponedJumpIfNot();
		if (offset != null) function.accept(offset);
		else pending.add(function);
	}

	@Override
	public void place() {
		offset = writer.getOffset();
		for (Consumer<Integer> c : pending)
			c.accept(offset);
		pending.clear();
	}

	@Override
	public void close() {
	}
}

public class ScopedCompiler implements FCompiler {
	private final ScopedCompiler parent;
	private final InstructionWriter writer;
	private final List<Pair<Type, Key>> locals = new ArrayList<>();
	private final Type returnType;
	private int params = 0;
	private Label breakLabel = null, continueLabel = null;
	
	public ScopedCompiler(Type returnType) {
		this(null, returnType);
	}
	
	private ScopedCompiler(ScopedCompiler parent, Type returnType) {
		this.parent = parent;
		if (parent == null)
			this.writer = new InstructionWriter();
		else
			this.writer = parent.writer;
		this.returnType = returnType;
	}

	private Integer getParentLocalCount() {
		return ((parent == null) ? 0 : (parent.locals.size() + parent.getParentLocalCount()));
	}
	
	private Pair<Type, Integer> findLocalVariable(Key name) {
		for (int i = 0; i < locals.size(); i++)
			if (locals.get(i).getSecond() == name)
				return new Pair<>(locals.get(i).getFirst(), getParentLocalCount() + i);
		if (parent != null) {
			return parent.findLocalVariable(name);
		} else {
			return null;
		}
	}

	@Override
	public void addParam(Type type, Key name) {
		addLocal(type, name);
		params++;
	}

	@Override
	public void addLocal(Type type, Key name) {
		locals.add(new Pair<>(type, name));
	}

	public void enableVarargs() {
		writer.enableVarargs();
	}
	
	public boolean isLocal(Key identifier) {
		return findLocalVariable(identifier) != null;
	}

	public FCompiler subscope() {
		return new ScopedCompiler(this, returnType);
	}
	
	@Override
	public Label openLabel() {
		return new ScopedCompilerLabel(this, writer);
	}
	
	@Override
	public Label openBreakLabel() {
		if (breakLabel == null)
			breakLabel = openLabel();
		return breakLabel;
	}
	
	@Override
	public Label getBreakLabel() {
		if (breakLabel != null)
			return breakLabel;
		else if (parent != null)
			return parent.getBreakLabel();
		else
			return null;
	}
	
	@Override
	public Label openContinueLabel() {
		if (continueLabel == null)
			continueLabel = openLabel();
		return continueLabel;
	}
	
	@Override
	public Label getContinueLabel() {
		if (continueLabel != null)
			return continueLabel;
		else if (parent != null)
			return parent.getContinueLabel();
		else
			return null;
	}

	@Override
	public void compileLoadThis() {
		writer.writeLoadThis();
	}
	
	@Override
	public void compileLoadVANext() {
		writer.writeLoadVANext();
	}
	
	@Override
	public void compileLoadVACount() {
		writer.writeLoadVACount();
	}
	
	@Override
	public void compileLoadConstant(Instance value) {
		writer.writeLoadConstant(value);
	}

	@Override
	public void compileLoadVariable(Key identifier) {
		Pair<Type, Integer> info = findLocalVariable(identifier);
		if (info == null) {
			compileLoadThis();
			compileDot(identifier);
		} else {
			writer.writeLoadLocal(info.getSecond());
		}
	}

	@Override
	public void compileStoreVariable(Key identifier) {
		Pair<Type, Integer> info = findLocalVariable(identifier);
		if (info == null) {
			compilePush();
			compileLoadThis();
			compileDotAssign(identifier);
		} else {
			writer.writeTypeCheck(info.getFirst());
			writer.writeStoreLocal(info.getSecond());
		}
	}

	@Override
	public void compilePush() {
		writer.writePush();
	}
	
	@Override
	public void compileReturn() {
		writer.writeTypeCheck(returnType);
		writer.writeReturn();
	}

	@Override
	public void compileDot(Key key) {
		writer.writeDot(key);
	}

	@Override
	public void compileDotAssign(Key key) {
		writer.writeDotAssign(key);
	}

	@Override
	public void compileIndex() {
		writer.writeIndex();
	}

	@Override
	public void compileIndexAssign() {
		writer.writeIndexAssign();
	}

	@Override
	public void compileScope(Key key) {
		writer.writeScope(key);
	}

	@Override
	public void compileCall(int args, boolean hasVarargs) {
		writer.writeCall(args, hasVarargs);
	}
	
	@Override
	public void compileDotCall(Key key, int args, boolean hasVarargs) {
		writer.writeDotCall(key, args, hasVarargs);
	}

	@Override
	public void compileNew(Key clazz, int args, boolean hasVarargs) {
		writer.writeNew(clazz, args, hasVarargs);
	}
	
	@Override
	public void compileOp(OperatorType type) {
		writer.writeOp(type);
	}

	@Override
	public void compileMakeList(ListType type, int length) {
		writer.writeMakeList(type, length);
	}
	
	@Override
	public void compileMakeMapping(int length) {
		writer.writeMakeMapping(length);
	}
	
	public Code finish() {
		/*
		 * TODO: Only do this if the programmer hasn't supplied their
		 *       own return statement!
		 */
		compileLoadConstant(Instance.getNil());
		compileReturn();
		writer.declareParamCount(params);
		return writer.finish();
	}
}
