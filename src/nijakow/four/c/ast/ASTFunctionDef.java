package nijakow.four.c.ast;

import nijakow.four.c.compiler.ScopedCompiler;
import nijakow.four.c.runtime.Blueprint;
import nijakow.four.c.runtime.Code;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;
import nijakow.four.c.runtime.fs.Filesystem;
import nijakow.four.util.Pair;

public class ASTFunctionDef extends ASTDefinition {
	private final Pair<Type, Key>[] args;
	private final ASTInstruction body;

	public ASTFunctionDef(Type type, Key name, Pair<Type, Key>[] args, ASTInstruction body) {
		super(type, name);
		this.args = args;
		this.body = body;
	}

	@Override
	public void compileInto(Blueprint blueprint, Filesystem fs) {
		ScopedCompiler compiler = new ScopedCompiler();	// TODO: body.openCompilation();
		for (Pair<Type, Key> arg : args) {
			compiler.addParam(arg.getFirst(), arg.getSecond());
		}
		body.compile(compiler);
		Code code = compiler.finish();
		blueprint.addMethod(getName(), code);
	}
}
