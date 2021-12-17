package nijakow.four.c.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import nijakow.four.c.ast.OperatorType;
import nijakow.four.c.runtime.Code;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;
import nijakow.four.util.Pair;

class ScopedCompilerLabel implements Label {
	private final ScopedCompiler compiler;
	private final InstructionWriter writer;
	private List<Consumer<Integer>> pending = new ArrayList<>();
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
	private int params = 0;
	
	public ScopedCompiler() {
		this(null);
	}
	
	private ScopedCompiler(ScopedCompiler parent) {
		this.parent = parent;
		if (parent == null)
			this.writer = new InstructionWriter();
		else
			this.writer = parent.writer;
	}

	private Integer getParentLocalCount() {
		return ((parent == null) ? 0 : (parent.locals.size() + parent.getParentLocalCount()));
	}
	
	private Integer findLocalVariable(Key name) {
		for (int i = 0; i < locals.size(); i++)
			if (locals.get(i).getSecond() == name)
				return getParentLocalCount() + i;
		if (parent != null) return parent.findLocalVariable(name);
		else return null;
	}

	@Override
	public void addParam(Type type, Key name) {
		addLocal(type, name);
		params++;
		// TODO: Increment parameter count by one
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
		return new ScopedCompiler(this);
	}
	
	@Override
	public Label openLabel() {
		return new ScopedCompilerLabel(this, writer);
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
	public void compileLoadConstant(Instance value) {
		writer.writeLoadConstant(value);
	}

	@Override
	public void compileLoadVariable(Key identifier) {
		Integer index = findLocalVariable(identifier);
		if (index == null) {
			compileLoadThis();
			compileDot(identifier);
		} else {
			writer.writeLoadLocal(index);
		}
	}

	@Override
	public void compileStoreVariable(Key identifier) {
		Integer index = findLocalVariable(identifier);
		if (index == null) {
			compilePush();
			compileLoadThis();
			compileDotAssign(identifier);
		} else {
			writer.writeStoreLocal(index);
		}
	}

	@Override
	public void compilePush() {
		writer.writePush();
	}
	
	@Override
	public void compileReturn() {
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
	public void compileOp(OperatorType type) {
		writer.writeOp(type);
	}

	public Code finish() {
		writer.declareParamCount(params);
		return writer.finish();
	}
}
