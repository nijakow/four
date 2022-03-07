package nijakow.four.share.lang.c.ast;

import nijakow.four.server.runtime.vm.code.CodeMeta;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.server.compiler.ScopedCompiler;
import nijakow.four.server.runtime.*;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.share.lang.c.SlotVisibility;
import nijakow.four.share.lang.c.parser.StreamPosition;
import nijakow.four.share.util.Pair;

public class ASTFunctionDef extends ASTDefinition {
	private final Pair<Type, Key>[] args;
	private final boolean hasVarargs;
	private final ASTInstruction body;

	public ASTFunctionDef(StreamPosition pos, String cDoc, SlotVisibility visibility, Type type, Key name, Pair<Type, Key>[] args, boolean hasVarargs, ASTInstruction body) {
		super(pos, cDoc, visibility, type, name);
		this.args = args;
		this.hasVarargs = hasVarargs;
		this.body = body;
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) throws CompilationException {
		Type[] argTypes = new Type[args.length];
		for (int x = 0; x < argTypes.length; x++)
			argTypes[x] = args[x].getFirst();
		ScopedCompiler compiler = new ScopedCompiler(new CodeMeta(getPos(), getCDoc(), getType(), argTypes));	// TODO: body.openCompilation();
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
