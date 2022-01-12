package nijakow.four.c.ast;

import nijakow.four.c.compiler.ScopedCompiler;
import nijakow.four.runtime.Blueprint;
import nijakow.four.runtime.Code;
import nijakow.four.runtime.Key;
import nijakow.four.runtime.Type;
import nijakow.four.runtime.fs.Filesystem;
import nijakow.four.util.Pair;

public class ASTFunctionDef extends ASTDefinition {
	private final Pair<Type, Key>[] args;
	private final boolean hasVarargs;
	private final ASTInstruction body;

	public ASTFunctionDef(Type type, Key name, Pair<Type, Key>[] args, boolean hasVarargs, ASTInstruction body) {
		super(type, name);
		this.args = args;
		this.hasVarargs = hasVarargs;
		this.body = body;
	}

	@Override
	public void compileInto(Blueprint blueprint, Filesystem fs) {
		ScopedCompiler compiler = new ScopedCompiler(getType());	// TODO: body.openCompilation();
		for (Pair<Type, Key> arg : args) {
			compiler.addParam(arg.getFirst(), arg.getSecond());
		}
		if (hasVarargs)
			compiler.enableVarargs();
		body.compile(compiler);
		Code code = compiler.finish();
		blueprint.addMethod(getName(), code);
	}
}
