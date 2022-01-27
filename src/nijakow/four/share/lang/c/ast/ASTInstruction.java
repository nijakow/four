package nijakow.four.share.lang.c.ast;

import nijakow.four.server.compiler.ScopedCompiler;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;

public abstract class ASTInstruction extends AST {
	void compile(FCompiler compiler) throws CompilationException {
		compilationError("Oof.");
	}

	public Code compileStandalone() throws CompilationException {
		ScopedCompiler compiler = new ScopedCompiler(Type.getAny());
		compile(compiler);
		return compiler.finish();
	}
}
