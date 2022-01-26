package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.server.compiler.ScopedCompiler;
import nijakow.four.server.runtime.*;
import nijakow.four.server.runtime.objects.Blueprint;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.share.lang.c.SlotVisibility;
import nijakow.four.share.util.Pair;

public class ASTFunctionDef extends ASTDefinition {
	private final Pair<Type, Key>[] args;
	private final boolean hasVarargs;
	private final ASTInstruction body;

	public ASTFunctionDef(SlotVisibility visibility, Type type, Key name, Pair<Type, Key>[] args, boolean hasVarargs, ASTInstruction body) {
		super(visibility, type, name);
		this.args = args;
		this.hasVarargs = hasVarargs;
		this.body = body;
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) throws CompilationException {
		ScopedCompiler compiler = new ScopedCompiler(getType());	// TODO: body.openCompilation();
		for (Pair<Type, Key> arg : args) {
			compiler.addParam(arg.getFirst(), arg.getSecond());
		}
		if (hasVarargs)
			compiler.enableVarargs();
		body.compile(compiler);
		Code code = compiler.finish();
		blueprint.addMethod(getVisibility(), getName(), code);
	}
}
